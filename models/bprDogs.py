import sys
import numpy as np
import pandas as pd
from math import ceil
from tqdm import trange
from subprocess import call
from itertools import islice
from sklearn.metrics import roc_auc_score
from sklearn.preprocessing import normalize
from sklearn.neighbors import NearestNeighbors
from scipy.sparse import csr_matrix, dok_matrix
import os
import psycopg2
from sqlalchemy import create_engine
import BPR


def create_matrix(data, users_col, items_col, ratings_col, threshold = None):
    """
    creates the sparse user-item interaction matrix,
    if the data is not in the format where the interaction only
    contains the positive items (indicated by 1), then use the 
    threshold parameter to determine which items are considered positive
    
    Parameters
    ----------
    data : DataFrame
        implicit rating data

    users_col : str
        user column name

    items_col : str
        item column name
    
    ratings_col : str
        implicit rating column name

    threshold : int, default None
        threshold to determine whether the user-item pair is 
        a positive feedback

    Returns
    -------
    ratings : scipy sparse csr_matrix, shape [n_users, n_items]
        user/item ratings matrix

    data : DataFrame
        implict rating data that retains only the positive feedback
        (if specified to do so)
    """
    if threshold is not None:
        data = data[data[ratings_col] >= threshold]
        data[ratings_col] = 1

    # this ensures each user has at least 2 records to construct a valid
    # train and test split in downstream process, note we might purge
    # some users completely during this process
    data_user_num_items = (data
                         .groupby('userid')
                         .agg(**{'num_items': ('perroid', 'count')})
                         .reset_index())
    data = data.merge(data_user_num_items, on='userid', how='inner')
    data = data[data['num_items'] > 1]
    
    for col in (items_col, users_col, ratings_col):
        data[col] = data[col].astype('category')

    ratings = csr_matrix((data[ratings_col],
                          (data[users_col].cat.codes, data[items_col].cat.codes)))
    ratings.eliminate_zeros()
    return ratings, data

def create_train_test(ratings, test_size = 0.2, seed = 1234):
    """
    split the user-item interactions matrix into train and test set
    by removing some of the interactions from every user and pretend
    that we never seen them
    
    Parameters
    ----------
    ratings : scipy sparse csr_matrix, shape [n_users, n_items]
        The user-item interactions matrix
    
    test_size : float between 0.0 and 1.0, default 0.2
        Proportion of the user-item interactions for each user
        in the dataset to move to the test set; e.g. if set to 0.2
        and a user has 10 interactions, then 2 will be moved to the
        test set
    
    seed : int, default 1234
        Seed for reproducible random splitting the 
        data into train/test set
    
    Returns
    ------- 
    train : scipy sparse csr_matrix, shape [n_users, n_items]
        Training set
    
    test : scipy sparse csr_matrix, shape [n_users, n_items]
        Test set
    """
    assert test_size < 1.0 and test_size > 0.0

    # Dictionary Of Keys based sparse matrix is more efficient
    # for constructing sparse matrices incrementally compared with csr_matrix
    train = ratings.copy().todok()
    test = dok_matrix(train.shape)
    
    # for all the users assign randomly chosen interactions
    # to the test and assign those interactions to zero in the training;
    # when computing the interactions to go into the test set, 
    # remember to round up the numbers (e.g. a user has 4 ratings, if the
    # test_size is 0.2, then 0.8 ratings will go to test, thus we need to
    # round up to ensure the test set gets at least 1 rating)
    rstate = np.random.RandomState(seed)
    for u in range(ratings.shape[0]):
        split_index = ratings[u].indices
        n_splits = ceil(test_size * split_index.shape[0])
        test_index = rstate.choice(split_index, size = n_splits, replace = False)
        test[u, test_index] = ratings[u, test_index]
        train[u, test_index] = 0
    
    train, test = train.tocsr(), test.tocsr()
    return train, test


def getSimilar(quantity):
    alchemyEngine   = create_engine('postgresql+psycopg2://postgres:password@127.0.0.1/doggynator', pool_recycle=3600)
    # Connect to PostgreSQL server
    dbConnection    = alchemyEngine.connect()
    # Read data from PostgreSQL database table and load into a DataFrame instance
    dataFrame       = pd.read_sql("select * from \"megusta\"", dbConnection)
    pd.set_option('display.expand_frame_repr', False)
    # Close the database connection
    dbConnection.close()
    items_col = 'perroid'
    users_col = 'userid'
    ratings_col = 'megusta'
    threshold = 3
    X, df = create_matrix(dataFrame, users_col, items_col, ratings_col, threshold)
    bpr_params = {
    'reg': 0.01,
    'learning_rate': 0.1,
    'n_iters': 160,
    'n_factors': 15,
    'batch_size': 1
    }
    bpr = BPR.BPR(**bpr_params)
    bpr.fit(X)
    return bpr.get_similar_items(N = quantity)
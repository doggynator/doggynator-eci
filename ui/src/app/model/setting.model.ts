export class setting {
    public option: string;
    public imgASrc: string;
    public imgAValue: string;
    public imgAOption: string;
    public imgADesc: string;
    public imgBSrc: string;
    public imgBValue: string;
    public imgBOption: string;
    public imgBDesc: string;

    constructor(option:string, imgASrc:string, imgAValue:string, imgBSrc:string,
      imgBValue:string, imgAOption: string, imgADesc: string,  imgBOption: string, imgBDesc: string){
        this.option = option;
        this.imgASrc = imgASrc;
        this.imgAValue = imgAValue;
        this.imgBSrc = imgBSrc;
        this.imgBValue = imgBValue;
        this.imgAOption = imgAOption;
        this.imgADesc = imgADesc;
        this.imgBOption = imgBOption;
        this.imgBDesc = imgBDesc;
    }
}

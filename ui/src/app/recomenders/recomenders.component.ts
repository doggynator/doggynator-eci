import { Component, OnInit } from '@angular/core';
import { Card } from '../model/card.model';
import { StorageService } from '../_services/storage.service';
import { UserService } from '../_services/user.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-recomenders',
  templateUrl: './recomenders.component.html',
  styleUrls: ['./recomenders.component.css']
})
export class RecomendersComponent implements OnInit {
  content: any;
  edad: String = '';
  nombre: String = '';
  fundacion: String = '';
  raza: String = '';
  tamano: String = '';
  sexo: String = '';
  color: String = '';
  pelaje: String = '';
  agresividad: String = '';
  ninos: String = '';
  otros: String = '';
  esterilizado: String = '';
  actividad: String = '';
  necesidades: String = '';
  img: String = '';
  id: String = '';
  index: number = 0;
  ulikes : any[] = [];
  liked : any[] = [];
  cards : Card[] = [];
  unliked : any[] = [];
  isLoading = false;


  constructor(private userService : UserService, private storageService : StorageService, private router: Router) { }

  ngOnInit(): void {
    this.isLoading = true;
    this.userService.getDogForUserFromModel(this.storageService.getUser().id).subscribe({
      next: data => {
        this.isLoading = false;
        console.log(data);
        this.content = data;
        this.loadData();
      },
      error: err => {
        this.isLoading = false;
        if (err.error) {
          try {
            const res = JSON.parse(err.error);
            this.content = res.message;
          } catch {
            this.content = `Error with status: ${err.status} - ${err.statusText}`;
          }
        } else {
          this.content = `Error with status: ${err.status}`;
        }
      }
    });

  }

  loadData(){
    if (this.content.length == 0){
      console.warn("No hay contenido para mostrar")
      this.cards = []
      return;
    }
    let cartas : Card[] = [];
    this.content.forEach( (element: {
      actividadfisica: number;
      sexo: String;
      color: String;
      pelaje: String;
      agresividad: String;
      otrosperros: number;
      esterilizado: number;
      necesidades: String;
      ninos: number;
      edad: String;
      nombre: String;
      fundacion: String;
      raza: String;
      tamano: string; perroid: String;
      }) => {
          console.log(element);

          this.id = element.perroid;
          this.edad = element.edad;
          this.nombre = element.nombre;
          this.fundacion = element.fundacion;
          this.raza = element.raza;
          this.tamano = element.tamano;
          this.sexo = element.sexo;
          this.color = element.color;
          this.pelaje = element.pelaje;
          this.agresividad = element.agresividad;
          if(element.ninos == 0){
            this.ninos = 'No';
          }else{
            this.ninos = 'Si';
          }
          if(element.otrosperros == 0){
            this.otros = 'No';
          }else{
            this.otros = 'Si';
          }
          if(element.esterilizado == 0){
            this.esterilizado = 'No';
          }else{
            this.esterilizado = 'Si';
          }if(element.actividadfisica == 0){
            this.actividad = 'No';
          }else{
            this.actividad = 'Si';
          }

          this.necesidades = element.necesidades;
          this.img = 'assets/resources/dogs/' + this.id + '.jpg';
          cartas.push(new Card(this.id ,this.edad, this.nombre, this.fundacion, this.raza, this.tamano, this.sexo, this.color,
            this.pelaje, this.agresividad, this.ninos, this.otros, this.esterilizado, this.actividad, this.necesidades, this.img));
          this.unliked.push({"option" : this.id , "value" : 0 });
        });
        this.cards = cartas;
	console.debug("Cards")
        console.log(this.cards);
        const ulike_clone  = Object.assign([], this.unliked);
        ulike_clone.push({"option" : "userid" , "value" : this.storageService.getUser().id})
        this.userService.saveULikes(ulike_clone).subscribe({
          next: data => {
            console.debug("Saved init data:", data)
            ulike_clone.splice(0);
          },
          error: err => {
            console.error("Fai saving init data", err)
            ulike_clone.splice(0);
          }
        });
  }
 onClick(i: any) {
    console.log(i);

    const name = document.getElementById(i)?.getAttribute('class');
    console.log(name);
    if(name=='bi bi-hand-thumbs-up'){
      this.ulikes.push({"option" : i , "value" : 1 });
      this.ulikes.push({"option" : "userid" , "value" : this.storageService.getUser().id})
      this.unliked = this.unliked.filter(obj => { return obj.option !== i });
      console.log(this.unliked);
      this.userService.saveULikes(this.ulikes).subscribe({
        next: data => {
          console.log(data);
          this.ulikes = [];
        },
        error: err => {
          console.error(err)
        }
      });
      document.getElementById(i)?.setAttribute('class', 'bi bi-heart-fill');
    }else{
      this.ulikes.push({"option" : i , "value" : 0 });
      this.ulikes.push({"option" : "userid" , "value" : this.storageService.getUser().id})
      this.unliked.push({"option" : i , "value" : 0 });
      this.userService.saveULikes(this.ulikes).subscribe({
        next: data => {
          console.log(data);
          this.ulikes = [];
        },
        error: err => {
          console.error(err)
        }
      });
      document.getElementById(i)?.setAttribute('class', 'bi bi-heart');
    }
  }

  onClick0(i: any) {
    console.log(i);

    const name = document.getElementById('thumbs-down-id-'+i)?.getAttribute('class');
    console.log(name);
    if(name=='bi bi-hand-thumbs-down'){
      this.ulikes.push({"option" : i , "value" : 0 });
      this.ulikes.push({"option" : "userid" , "value" : this.storageService.getUser().id})
      this.userService.saveULikes(this.ulikes).subscribe({
        next: data => {
          console.log(data);
          this.ulikes = [];
        },
        error: err => {
          console.error(err)
        }
      });
      document.getElementById('thumbs-down-id-'+i)?.setAttribute('class', 'bi bi-hand-thumbs-down-fill');
      document.getElementById('thumbs-up-id-'+i)?.setAttribute('class', 'bi bi-hand-thumbs-up');
    }else{
      this.ulikes.push({"option" : i , "value" : 1 });
      this.ulikes.push({"option" : "userid" , "value" : this.storageService.getUser().id})
      this.userService.saveULikes(this.ulikes).subscribe({
        next: data => {
          console.log(data);
          this.ulikes = [];
        },
        error: err => {
          console.error(err)
        }
      });
      document.getElementById('thumbs-down-id-'+i)?.setAttribute('class', 'bi bi-hand-thumbs-down');
    }
  }

  onClick1(i: any) {
    console.log(i);

    const name = document.getElementById('thumbs-up-id-'+i)?.getAttribute('class');
    console.log(name);
    if(name=='bi bi-hand-thumbs-up'){
      this.ulikes.push({"option" : i , "value" : 1 });
      this.ulikes.push({"option" : "userid" , "value" : this.storageService.getUser().id})
      this.userService.saveULikes(this.ulikes).subscribe({
        next: data => {
          console.log(data);
          this.ulikes = [];
        },
        error: err => {
          console.error(err)
        }
      });
      document.getElementById('thumbs-up-id-'+i)?.setAttribute('class', 'bi bi-hand-thumbs-up-fill');
      document.getElementById('thumbs-down-id-'+i)?.setAttribute('class', 'bi bi-hand-thumbs-down');
    }else{
      this.ulikes.push({"option" : i , "value" : 0 });
      this.ulikes.push({"option" : "userid" , "value" : this.storageService.getUser().id})
      this.userService.saveULikes(this.ulikes).subscribe({
        next: data => {
          console.log(data);
          this.ulikes = [];
        },
        error: err => {
          console.error(err)
        }
      });
      document.getElementById('thumbs-up-id-'+i)?.setAttribute('class', 'bi bi-hand-thumbs-up');
    }
  }

  moreRecommendation(){
    this.isLoading = true;
    console.log(this.unliked);
    this.unliked.push({"option" : "userid" , "value" : this.storageService.getUser().id})
    this.userService.saveULikes(this.unliked).subscribe({
      next: data => {
        console.log(data);
        this.ulikes = [];
        this.unliked= [];
        this.router.navigate(['/recomenders']).then(() => {
          window.location.reload();
        });
      },
      error: err => {
        console.error(err)
      }
    });
  }

}

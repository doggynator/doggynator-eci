import { Component, OnInit } from '@angular/core';
import { Card } from '../model/card.model';
import { StorageService } from '../_services/storage.service';
import { UserService } from '../_services/user.service';

@Component({
  selector: 'app-cards',
  templateUrl: './cards.component.html',
  styleUrls: ['./cards.component.css']
})
export class CardsComponent implements OnInit {
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
  cards : Card[] = [ new Card('1','2', 'Naranjo', 'Peluditos con futuro', 'Criollo', "Intermedio", 'Macho', 'Mixto',
  'Corto', 'Bajo', 'Si', 'Si', 'Si', 'No', 'No presenta', 'https://drive.google.com/file/d/1zKQcJ7v5QQzcMU-VW5gxVCoUUgPymxRn/view'),
  new Card('2','2', 'Naranjo', 'Peluditos con futuro', 'Criollo', "Intermedio", 'Macho', 'Mixto',
  'Corto', 'Bajo', 'Si', 'Si', 'Si', 'No', 'No presenta', 'https://drive.google.com/file/d/1zKQcJ7v5QQzcMU-VW5gxVCoUUgPymxRn/view'),
  new Card('3','2', 'Naranjo', 'Peluditos con futuro', 'Criollo', "Intermedio", 'Macho', 'Mixto',
  'Corto', 'Bajo', 'Si', 'Si', 'Si', 'No', 'No presenta', 'https://drive.google.com/file/d/1zKQcJ7v5QQzcMU-VW5gxVCoUUgPymxRn/view'),
  new Card('4','2', 'Naranjo', 'Peluditos con futuro', 'Criollo', "Intermedio", 'Macho', 'Mixto',
  'Corto', 'Bajo', 'Si', 'Si', 'Si', 'No', 'No presenta', 'assets/resources/dogs/1.jpg'),
  new Card('5','2', 'Naranjo', 'Peluditos con futuro', 'Criollo', "Intermedio", 'Macho', 'Mixto',
  'Corto', 'Bajo', 'Si', 'Si', 'Si', 'No', 'No presenta', 'assets/resources/dogs/1.jpg'),
  new Card('6','2', 'Naranjo', 'Peluditos con futuro', 'Criollo', "Intermedio", 'Macho', 'Mixto',
  'Corto', 'Bajo', 'Si', 'Si', 'Si', 'No', 'No presenta', 'assets/resources/dogs/1.jpg'),
  new Card('7','2', 'Naranjo', 'Peluditos con futuro', 'Criollo', "Intermedio", 'Macho', 'Mixto',
  'Corto', 'Bajo', 'Si', 'Si', 'Si', 'No', 'No presenta', 'assets/resources/dogs/1.jpg'),
  new Card('8','2', 'Naranjo', 'Peluditos con futuro', 'Criollo', "Intermedio", 'Macho', 'Mixto',
  'Corto', 'Bajo', 'Si', 'Si', 'Si', 'No', 'No presenta', 'assets/resources/dogs/1.jpg'),
  new Card('9','2', 'Naranjo', 'Peluditos con futuro', 'Criollo', "Intermedio", 'Macho', 'Mixto',
  'Corto', 'Bajo', 'Si', 'Si', 'Si', 'No', 'No presenta', 'assets/resources/dogs/1.jpg'),
  new Card('10','2', 'Naranjo', 'Peluditos con futuro', 'Criollo', "Intermedio", 'Macho', 'Mixto',
  'Corto', 'Bajo', 'Si', 'Si', 'Si', 'No', 'No presenta', 'assets/resources/dogs/1.jpg')]
  isLoading = false;

  constructor(private userService : UserService, private storageService : StorageService) { }

  ngOnInit(): void {
    this.isLoading = true;
    this.userService.getDogsForUser(this.storageService.getUser().id).subscribe({
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
      return;
    }
    console.log(this.content[0]);
    this.id = this.content[0].perroid;
    this.edad = this.content[0].edad;
    this.nombre = this.content[0].nombre;
    this.fundacion = this.content[0].fundacion;
    this.raza = this.content[0].raza;
    this.tamano = this.content[0].tamano;
    this.sexo = this.content[0].sexo;
    this.color = this.content[0].color;
    this.pelaje = this.content[0].pelaje;
    this.agresividad = this.content[0].agresividad;
    if(this.content[0].ninos == 0){
      this.ninos = 'No';
    }else{
      this.ninos = 'Si';
    }
    if(this.content[0].otrosperros == 0){
      this.otros = 'No';
    }else{
      this.otros = 'Si';
    }
    if(this.content[0].esterilizado == 0){
      this.esterilizado = 'No';
    }else{
      this.esterilizado = 'Si';
    }if(this.content[0].actividadfisica == 0){
      this.actividad = 'No';
    }else{
      this.actividad = 'Si';
    }

    this.necesidades = this.content[0].necesidades;
    this.img = 'assets/resources/dogs/' + this.id + '.jpg';
  }
  onClick(i: any) {
    this.isLoading = true;
    console.log(i);
    console.log("index ", this.index);
    console.log("this.cards.length ", this.content.length);
    this.ulikes.push({"option" : this.id , "value" : i})
    if(this.index != this.content.length-1){
      this.index ++;
      this.id = this.content[this.index].perroid;
        this.edad = this.content[this.index].edad;
        this.nombre = this.content[this.index].nombre;
        this.fundacion = this.content[this.index].fundacion;
        this.raza = this.content[this.index].raza;
        this.tamano = this.content[this.index].tamano;
        this.sexo = this.content[this.index].sexo;
        this.color = this.content[this.index].color;
        this.pelaje = this.content[this.index].pelaje;
        this.agresividad = this.content[this.index].agresividad;
        if(this.content[this.index].ninos == 0){
          this.ninos = 'No';
        }else{
          this.ninos = 'Si';
        }
        if(this.content[this.index].otrosperros == 0){
          this.otros = 'No';
        }else{
          this.otros = 'Si';
        }
        if(this.content[this.index].esterilizado == 0){
          this.esterilizado = 'No';
        }else{
          this.esterilizado = 'Si';
        }if(this.content[this.index].actividadfisica == 0){
          this.actividad = 'No';
        }else{
          this.actividad = 'Si';
        }
        this.necesidades = this.content[this.index].necesidades;
        this.img = 'assets/resources/dogs/' + this.id + '.jpg';
        this.isLoading = false;
    }else{
      this.ulikes.push({"option" : "userid" , "value" : this.storageService.getUser().id})
      this.userService.saveULikes(this.ulikes).subscribe({
      next: data => {
        this.isLoading = false;
        console.log(data);
        window.location.assign("/recomenders");
      },
      error: err => {
        this.isLoading = false;
        console.error(err)
      }
    });
    }
  }
}

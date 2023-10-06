import { Component, OnInit } from '@angular/core';
import { setting } from '../model/setting.model';
import { StorageService } from '../_services/storage.service';
import { UserService } from '../_services/user.service';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.css']
})
export class SettingsComponent implements OnInit {
  content: any;
  form: any = {
    salario: "Seleccione una opción",
    estrato: "Seleccione una opción",
    viaja: 0,
    edad: null
  };
  imgSrcA: string = '';
  imgOptA: string = '';
  imgDescA: string = '';
  imgValueA: string = '0';
  imgSrcB: string = '';
  imgOptB: string = '';
  imgDescB: string = '';
  imgValueB: string = '1';
  index: number = 0;
  favVisible = true;
  upreferences : any[] = [];
    settings : setting[] = [
    new setting('familia', '../../assets/resources/FamiliaConHijos.png', '0', '../../assets/resources/FamiliaSinHijos.png', '1',
    'con hijos','¿Actualmente tienes hijos o deseas agregar algún miembro a tu familia en el futuro?', 'Sin hijos', '¿Tu definición de familia no incluye hijos?'),
    new setting('casa', '../../assets/resources/casa.png', '0', '../../assets/resources/apto.png', '1',
    'casa', 'Vives en una casa?', 'Apartamento', 'Vives en un apartamento?'),
    new setting('estilo', '../../assets/resources/Ordenado.png', '0', '../../assets/resources/desorganizado.png', '1',
    'ordenado', '¿Eres una persona que da importancia al orden en sus espacios?', 'no tanto', '¿Eres una persona a la que no es relevante la organización de sus espacios?'),
    new setting('salir', '../../assets/resources/indoorsy.png', '0', '../../assets/resources/outdoors.png', '1',
    'Indoor', 'Te gustan y buscas realizar actividades en la casa?', 'outdoors', 'Prefieres las actividades extremas/al aire libre'),
    new setting('actividad', '../../assets/resources/Netflix.png', '0', '../../assets/resources/Salir.png', '1',
    'Caserito', 'Buscas y te gusta realizar actividades en la casa', 'Salir', 'Te gusta la fiesta, salir con tus amigos o buscas actividades fuera de tu casa? '),
    new setting('trabajo', '../../assets/resources/wfh.png', '0', '../../assets/resources/oficina.png', '1',
    'Remoto', 'Trabajas principalmente desde la casa?', 'Oficina', 'Trabajas principalmente desde la oficina? ')

  ];

  constructor(private userService : UserService, private storageService : StorageService) { }

  ngOnInit(): void {

    this.userService.getUPreferences(this.storageService.getUser().id).subscribe({
      next: data => {
        this.content = data;
        this.loadData();
      },
      error: err => {
        if (err.error) {
          try {
            const res = JSON.parse(err.error);
          } catch {
          }
        }
      }
    });

    var formItem = document.getElementById("formId");
      if(formItem != null){
        formItem.style!.display = "none";
      }
    var randomItem = document.getElementById("randomId");
      if(randomItem != null){
        randomItem.style!.display = "none";
      }

    this.imgSrcA = this.settings[0].imgASrc;
    this.imgOptA = this.settings[0].imgAOption;
    this.imgDescA = this.settings[0].imgADesc;
    this.imgSrcB = this.settings[0].imgBSrc;
    this.imgOptB = this.settings[0].imgBOption;
    this.imgDescB = this.settings[0].imgBDesc;
    document.getElementById('imgAId')!.style.backgroundImage = 'url(' + this.imgSrcA + ')';
    document.getElementById('imgBId')!.style.backgroundImage = 'url(' + this.imgSrcB + ')';

  }
  loadData(){
    if (this.content.length == 0){
      console.warn("No hay contenido para mostrar")
      return;
    }
    const fpy = this.content.filter((obj: { option: any; value: any; }) => { return obj.option == 'fpy' });
    console.log(fpy);
    console.log("fpy cargado");
    if(fpy[0].value == '1'){
      console.log("fpy = 1");
      this.content.forEach((element: {
        option: String;
        value: String;
      }) => {

        console.log(element);
        if(element.option == "actividad" && element.value !== "null"){
          console.log("actividad no es null");
          this.upreferences = this.upreferences.filter(obj => { return obj.option !== element.option });
        }
        if(element.option == "viajes"){
          if(element.value == "true"){
            console.log("viaja es true");
            this.form.viaja = 1;
          }

        }
      });
    }
  }

  onClick(i: any) {
    console.log(i);
    this.upreferences.push({"option" : this.settings[this.index].option , "value" : i})
    console.log("index ", this.index);
    console.log("this.settings.length ", this.settings.length);
    if(this.index != this.settings.length-1){
      this.index ++;
      this.imgSrcA = this.settings[this.index].imgASrc;
      this.imgSrcB = this.settings[this.index].imgBSrc;
      this.imgOptA = this.settings[this.index].imgAOption;
      this.imgDescA = this.settings[this.index].imgADesc;
      this.imgOptB = this.settings[this.index].imgBOption;
      this.imgDescB = this.settings[this.index].imgBDesc;
      document.getElementById('imgAId')!.style.backgroundImage = 'url(' + this.imgSrcA + ')';
      document.getElementById('imgBId')!.style.backgroundImage = 'url(' + this.imgSrcB + ')';
    }else{
      var optionsItem = document.getElementById("optionsId");
      if(optionsItem != null){
        optionsItem.style!.display = "none";
      }
      var formItem = document.getElementById("formId");
      if(formItem != null){
        formItem.style!.display = "block";
      }
      var randomItem = document.getElementById("randomId");
      if(randomItem != null){
        randomItem.style!.display = "none";
      }
    }
  }

  callRandomDogs() {
    console.log("call random dogs : " + this.upreferences);
    const { salario, estrato, viaja, edad } = this.form;
    console.log(salario);
    console.log(estrato);
    console.log(viaja);
    console.log(edad);
    if(this.form.edad != null && this.form.edad > 18 && this.form.edad < 80 && this.form.salario != "Seleccione una opción" && this.form.estrato != "Seleccione una opción"){
      this.upreferences.push({"option" : "estrato" , "value" : estrato})
      this.upreferences.push({"option" : "salario" , "value" : salario})
      this.upreferences.push({"option" : "viajes" , "value" : viaja})
      this.upreferences.push({"option" : "edad" , "value" : edad})
      this.upreferences.push({"option" : "userid" , "value" : this.storageService.getUser().id})

      this.userService.saveUPreferences(this.upreferences).subscribe({
        next: data => {
          console.log(data);
          window.location.assign("/cards");
          var optionsItem = document.getElementById("optionsId");
          if(optionsItem != null){
            optionsItem.style!.display = "none";
          }
          var formItem = document.getElementById("formId");
          if(formItem != null){
            formItem.style!.display = "none";
          }
          var randomItem = document.getElementById("randomId");
          if(randomItem != null){
            randomItem.style!.display = "block";
          }
        },
        error: err => {

        }
      });
    }else{
      if(this.form.edad == null || this.form.edad < 18 || this.form.edad > 80){
        window.alert("Edad es requerida, debe ser mayor a 18 y menor a 80");
      }else if(this.form.salario == "Seleccione una opción"){
        window.alert("Por favor, seleccione rango salarial");
      }else if(this.form.estrato == "Seleccione una opción"){
        window.alert("Por favor, seleccione un Estrato");
      }


    }

  }
}

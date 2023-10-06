export class Card {
    public perroid: String = '';
    public edad: String = '';
    public nombre: String = '';
    public fundacion: String = '';
    public raza: String = '';
    public tamano: String = '';
    public sexo: String = '';
    public color: String = '';
    public pelaje: String = '';
    public agresividad: String = '';
    public ninos: String = '';
    public otros: String = '';
    public esterilizado: String = '';
    public actividad: String = '';
    public necesidades: String = '';
    public imagePath: String = '';

    constructor(id: String, edad: String, nombre: String, fundacion: String, raza: String, tamano: String, sexo: String
      , color: String, pelaje: String, agresividad: String, ninos: String, otros: String, esterilizado: String
      , actividad: String, necesidades: String, imagePath: String){
        this.perroid = id;
        this.edad = edad;
        this.nombre = nombre;
        this.fundacion = fundacion;
        this.raza = raza;
        this.tamano = tamano;
        this.sexo = sexo;
        this.color = color;
        this.pelaje = pelaje;
        this.agresividad = agresividad;
        this.ninos = ninos;
        this.otros = otros;
        this.esterilizado = esterilizado;
        this.actividad = actividad;
        this.necesidades = necesidades;
        this.imagePath = imagePath;

    }
}

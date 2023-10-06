# DOGGYNATOR

## Descripción

Doggynator es una aplicación diseñada para la recolección de datos y la validación de recomendaciones. La aplicación consta de varias capas que trabajan juntas para lograr su funcionalidad.

### Doggynator-gui (UI)

Doggynator-gui es la capa de interfaz de usuario que permite la interacción con los usuarios. Esta capa recopila la información ingresada por los usuarios y la dirige a las capas subyacentes para su procesamiento.

### Doggynator-api (API)

Doggynator-api es la capa de backend de la aplicación. Esta capa es responsable de recibir solicitudes de los usuarios y realizar llamadas a los servicios expuestos por aplicaciones de terceros. Por ejemplo, se encarga de solicitar información a Facebook y entregarla a los modelos de perfilamiento. Además, guarda los datos de terceros en la base de datos para su posterior uso y entrega los resultados a la capa frontal.

### Doggynator-py (Models)

Doggynator-py forma parte de la capa de backend y se encarga de procesar los datos recopilados de Facebook. Enriquece los perfiles de los usuarios con esta información y contiene los diferentes algoritmos de recomendación utilizados en la aplicación.

### Base de Datos PostgreSQL (DB)

La base de datos PostgreSQL es parte de la tercera capa de la aplicación y se encarga de almacenar los datos transformados y necesarios para el funcionamiento de la aplicación. Los modelos utilizan estos datos para su aprendizaje y toma de decisiones.

### Facebook API

La aplicación Facebook API también forma parte de la tercera capa y se utiliza para recopilar datos de Facebook. Esta API obtiene información de grupos, feeds, edad, fecha de nacimiento y correo electrónico de los usuarios, que luego se utilizan en el proceso de perfilamiento y recomendación.

## Instalación

Para instalar Doggynator y sus dependencias, sigue estos pasos:

1. Clona este repositorio en tu máquina local.
   ```
   git clone https://github.com/doggynator/doggynator.git
   ```

2. Instala las dependencias de Python utilizando pip.
   ```
   pip install -r requirements.txt
   ```

3. Configura las credenciales de acceso a la base de datos y a las APIs de terceros en los archivos de configuración correspondientes.

4. Ejecuta la aplicación Doggynator.

## Uso

1. Accede a la interfaz de usuario de Doggynator a través de tu navegador web.

2. Ingresa la información requerida y realiza las interacciones deseadas.

3. Doggynator procesará tus solicitudes y te proporcionará recomendaciones basadas en los datos recopilados.

## Contribución

Si deseas contribuir al desarrollo de Doggynator, sigue estos pasos:

1. Haz un fork del repositorio Doggynator en GitHub.

2. Clona tu repositorio fork en tu máquina local.
   ```
   git clone https://github.com/doggynator/doggynator.git
   ```

3. Crea una rama (branch) para tu contribución.
   ```
   git checkout -b feature/nueva-funcionalidad
   ```

4. Realiza tus cambios y asegúrate de que todo funcione correctamente.

5. Haz commit de tus cambios y envía un pull request a la rama principal del repositorio original.

6. Tu contribución será revisada y, si es aceptada, se fusionará con el repositorio principal.

## Contacto

Si tienes preguntas o comentarios sobre Doggynator, no dudes en ponerte en contacto con nosotros a traves de github.

¡Gracias por usar Doggynator!





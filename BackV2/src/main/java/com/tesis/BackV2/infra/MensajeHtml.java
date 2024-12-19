package com.tesis.BackV2.infra;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class MensajeHtml {

    private String mensajeCorreo;

    public MensajeHtml(String mensaje) {
        this.mensajeCorreo = mensaje;
    }

    public String mensajeCreacionCuenta (String nombres, String usuario, String contrasena) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "  <meta charset=\"UTF-8\">\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "  <title>Notificación de Credenciales</title>\n" +
                "  <style>\n" +
                "    body {\n" +
                "      font-family: Arial, sans-serif;\n" +
                "      margin: 0;\n" +
                "      padding: 0;\n" +
                "      background-color: #f9f9f9;\n" +
                "      color: #333;\n" +
                "      line-height: 1.6;\n" +
                "    }\n" +
                "\n" +
                "    .email-container {\n" +
                "      max-width: 600px;\n" +
                "      margin: 30px auto;\n" +
                "      background: #ffffff;\n" +
                "      border: 1px solid #ddd;\n" +
                "      border-radius: 10px;\n" +
                "      box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);\n" +
                "      overflow: hidden;\n" +
                "    }\n" +
                "\n" +
                "    .email-header {\n" +
                "      background: #4CAF50;\n" +
                "      color: #ffffff;\n" +
                "      padding: 20px;\n" +
                "      text-align: center;\n" +
                "      font-size: 24px;\n" +
                "    }\n" +
                "\n" +
                "    .email-body {\n" +
                "      padding: 20px;\n" +
                "    }\n" +
                "\n" +
                "    .email-body h2 {\n" +
                "      margin-top: 0;\n" +
                "      color: #4CAF50;\n" +
                "    }\n" +
                "\n" +
                "    .email-body p {\n" +
                "      margin: 10px 0;\n" +
                "    }\n" +
                "\n" +
                "    .email-body .credentials {\n" +
                "      background: #f3f3f3;\n" +
                "      padding: 15px;\n" +
                "      border-radius: 5px;\n" +
                "      margin-top: 15px;\n" +
                "    }\n" +
                "\n" +
                "    .email-body .credentials p {\n" +
                "      margin: 5px 0;\n" +
                "      font-size: 18px;\n" +
                "    }\n" +
                "\n" +
                "    .email-body .credentials span {\n" +
                "      font-weight: bold;\n" +
                "      color: #333;\n" +
                "    }\n" +
                "\n" +
                "    .email-footer {\n" +
                "      background: #f3f3f3;\n" +
                "      text-align: center;\n" +
                "      padding: 10px;\n" +
                "      font-size: 14px;\n" +
                "      color: #777;\n" +
                "    }\n" +
                "\n" +
                "    .email-footer a {\n" +
                "      color: #4CAF50;\n" +
                "      text-decoration: none;\n" +
                "    }\n" +
                "\n" +
                "    @media (max-width: 600px) {\n" +
                "      .email-body {\n" +
                "        padding: 15px;\n" +
                "      }\n" +
                "\n" +
                "      .email-header {\n" +
                "        font-size: 20px;\n" +
                "        padding: 15px;\n" +
                "      }\n" +
                "    }\n" +
                "  </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "  <div class=\"email-container\">\n" +
                "    <div class=\"email-header\">\n" +
                "      Notificación de Credenciales\n" +
                "    </div>\n" +
                "    <div class=\"email-body\">\n" +
                "      <h2>Hola, "+ nombres +"</h2>\n" +
                "      <p>Hemos generado las credenciales para tu acceso. A continuación, encontrarás los detalles:</p>\n" +
                "      <div class=\"credentials\">\n" +
                "        <p><span>Usuario:</span> "+ usuario +"</p>\n" +
                "        <p><span>Contraseña:</span> "+ contrasena +"</p>\n" +
                "      </div>\n" +
                "      <p>Por motivos de seguridad, te recomendamos cambiar tu contraseña después de iniciar sesión.</p>\n" +
                "      <p>Si no solicitaste estas credenciales, ignora este mensaje.</p>\n" +
                "    </div>\n" +
                "    <div class=\"email-footer\">\n" +
                "      &copy; 2024 - Tu Aplicación. Todos los derechos reservados.<br>\n" +
                "      <a href=\"#\">Política de Privacidad</a> | <a href=\"#\">Términos de Servicio</a>\n" +
                "    </div>\n" +
                "  </div>\n" +
                "</body>\n" +
                "</html>\n";
    }

    public String mensajeCitacionPruebaIns (String nombreEstudiante, String nombrePrueba, String descripcionPrueba, String fechaPrueba) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "  <meta charset=\"UTF-8\">\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "  <title>Citación a Prueba</title>\n" +
                "  <style>\n" +
                "    body {\n" +
                "      font-family: Arial, sans-serif;\n" +
                "      margin: 0;\n" +
                "      padding: 0;\n" +
                "      background-color: #f4f4f4;\n" +
                "      color: #333;\n" +
                "      line-height: 1.5;\n" +
                "    }\n" +
                "\n" +
                "    .email-container {\n" +
                "      max-width: 600px;\n" +
                "      margin: 20px auto;\n" +
                "      background: #ffffff;\n" +
                "      border: 1px solid #ddd;\n" +
                "      border-radius: 10px;\n" +
                "      box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);\n" +
                "      overflow: hidden;\n" +
                "    }\n" +
                "\n" +
                "    .email-header {\n" +
                "      background: #0056b3;\n" +
                "      color: #ffffff;\n" +
                "      text-align: center;\n" +
                "      padding: 20px;\n" +
                "      font-size: 22px;\n" +
                "    }\n" +
                "\n" +
                "    .email-body {\n" +
                "      padding: 20px;\n" +
                "    }\n" +
                "\n" +
                "    .email-body h2 {\n" +
                "      margin: 0;\n" +
                "      color: #0056b3;\n" +
                "    }\n" +
                "\n" +
                "    .email-body p {\n" +
                "      margin: 10px 0;\n" +
                "    }\n" +
                "\n" +
                "    .email-body .test-details {\n" +
                "      margin: 15px 0;\n" +
                "      padding: 15px;\n" +
                "      background: #f9f9f9;\n" +
                "      border-left: 5px solid #0056b3;\n" +
                "      border-radius: 5px;\n" +
                "    }\n" +
                "\n" +
                "    .email-body .test-details p {\n" +
                "      margin: 5px 0;\n" +
                "      font-size: 16px;\n" +
                "    }\n" +
                "\n" +
                "    .email-body .test-details span {\n" +
                "      font-weight: bold;\n" +
                "      color: #333;\n" +
                "    }\n" +
                "\n" +
                "    .email-footer {\n" +
                "      background: #f4f4f4;\n" +
                "      text-align: center;\n" +
                "      padding: 10px;\n" +
                "      font-size: 14px;\n" +
                "      color: #666;\n" +
                "    }\n" +
                "\n" +
                "    .email-footer a {\n" +
                "      color: #0056b3;\n" +
                "      text-decoration: none;\n" +
                "    }\n" +
                "\n" +
                "    @media (max-width: 600px) {\n" +
                "      .email-body {\n" +
                "        padding: 15px;\n" +
                "      }\n" +
                "\n" +
                "      .email-header {\n" +
                "        font-size: 20px;\n" +
                "        padding: 15px;\n" +
                "      }\n" +
                "    }\n" +
                "  </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "  <div class=\"email-container\">\n" +
                "    <div class=\"email-header\">\n" +
                "      Citación a Prueba Académica\n" +
                "    </div>\n" +
                "    <div class=\"email-body\">\n" +
                "      <h2>Estimado(a) Representante de " + nombreEstudiante + "</h2>\n" +
                "      <p>Nos comunicamos para informarle que su representado(a) ha sido citado a una prueba académica. A continuación, encontrará los detalles:</p>\n" +
                "      <div class=\"test-details\">\n" +
                "        <p><span>Nombre de la Prueba:</span> " + nombrePrueba +"</p>\n" +
                "        <p><span>Descripción:</span> " + descripcionPrueba + "</p>\n" +
                "        <p><span>Fecha de Citación:</span> "+ fechaPrueba +"</p>\n" +
                "      </div>\n" +
                "      <p>Por favor, asegúrese de que su representado(a) asista puntualmente y lleve los materiales necesarios.</p>\n" +
                "      <p>Si tiene alguna duda o necesita más información, no dude en contactarnos.</p>\n" +
                "    </div>\n" +
                "    <div class=\"email-footer\">\n" +
                "      &copy; 2024 - Institución Educativa. Todos los derechos reservados.<br>\n" +
                "      <a href=\"#\">Contáctanos</a> | <a href=\"#\">Política de Privacidad</a>\n" +
                "    </div>\n" +
                "  </div>\n" +
                "</body>\n" +
                "</html>\n";
    }

    public String mensajeAprobacionPruebaIns (String nombreEstudiante,
                                              String nombrePrueba,
                                              String descripcionPrueba,
                                              String fechaAprobacion,
                                              String nombreProximaPrueba,
                                              String descripcionProximaPrueba,
                                              String fechaProximaPrueba) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "  <meta charset=\"UTF-8\">\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "  <title>Notificación de Aprobación</title>\n" +
                "  <style>\n" +
                "    body {\n" +
                "      font-family: Arial, sans-serif;\n" +
                "      margin: 0;\n" +
                "      padding: 0;\n" +
                "      background-color: #f4f4f4;\n" +
                "      color: #333;\n" +
                "      line-height: 1.5;\n" +
                "    }\n" +
                "\n" +
                "    .email-container {\n" +
                "      max-width: 600px;\n" +
                "      margin: 20px auto;\n" +
                "      background: #ffffff;\n" +
                "      border: 1px solid #ddd;\n" +
                "      border-radius: 10px;\n" +
                "      box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);\n" +
                "      overflow: hidden;\n" +
                "    }\n" +
                "\n" +
                "    .email-header {\n" +
                "      background: #28a745;\n" +
                "      color: #ffffff;\n" +
                "      text-align: center;\n" +
                "      padding: 20px;\n" +
                "      font-size: 22px;\n" +
                "    }\n" +
                "\n" +
                "    .email-body {\n" +
                "      padding: 20px;\n" +
                "    }\n" +
                "\n" +
                "    .email-body h2 {\n" +
                "      margin: 0;\n" +
                "      color: #28a745;\n" +
                "    }\n" +
                "\n" +
                "    .email-body p {\n" +
                "      margin: 10px 0;\n" +
                "    }\n" +
                "\n" +
                "    .achievement, .next-test {\n" +
                "      margin: 15px 0;\n" +
                "      padding: 15px;\n" +
                "      background: #f9f9f9;\n" +
                "      border-left: 5px solid #28a745;\n" +
                "      border-radius: 5px;\n" +
                "    }\n" +
                "\n" +
                "    .achievement span, .next-test span {\n" +
                "      font-weight: bold;\n" +
                "      color: #333;\n" +
                "    }\n" +
                "\n" +
                "    .next-test {\n" +
                "      border-left: 5px solid #007bff;\n" +
                "    }\n" +
                "\n" +
                "    .next-test h3 {\n" +
                "      color: #007bff;\n" +
                "      margin-top: 0;\n" +
                "    }\n" +
                "\n" +
                "    .email-footer {\n" +
                "      background: #f4f4f4;\n" +
                "      text-align: center;\n" +
                "      padding: 10px;\n" +
                "      font-size: 14px;\n" +
                "      color: #666;\n" +
                "    }\n" +
                "\n" +
                "    .email-footer a {\n" +
                "      color: #28a745;\n" +
                "      text-decoration: none;\n" +
                "    }\n" +
                "\n" +
                "    @media (max-width: 600px) {\n" +
                "      .email-body {\n" +
                "        padding: 15px;\n" +
                "      }\n" +
                "\n" +
                "      .email-header {\n" +
                "        font-size: 20px;\n" +
                "        padding: 15px;\n" +
                "      }\n" +
                "    }\n" +
                "  </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "  <div class=\"email-container\">\n" +
                "    <div class=\"email-header\">\n" +
                "      ¡Felicidades por tu logro!\n" +
                "    </div>\n" +
                "    <div class=\"email-body\">\n" +
                "      <h2>Estimado(a) Representante de "+ nombreEstudiante +"</h2>\n" +
                "      <p>Nos complace informarle que su representado(a) ha aprobado exitosamente la siguiente prueba académica:</p>\n" +
                "      <div class=\"achievement\">\n" +
                "        <p><span>Nombre de la Prueba:</span> "+ nombrePrueba +"</p>\n" +
                "        <p><span>Descripción:</span> "+ descripcionPrueba +"</p>\n" +
                "        <p><span>Fecha de Aprobación:</span> "+ fechaAprobacion +"</p>\n" +
                "      </div>\n" +
                "      <p>Además, le informamos sobre la próxima evaluación en la que su representado(a) participará:</p>\n" +
                "      <div class=\"next-test\">\n" +
                "        <h3>Próxima Prueba:</h3>\n" +
                "        <p><span>Nombre:</span> "+nombreProximaPrueba+"</p>\n" +
                "        <p><span>Descripción:</span> "+descripcionProximaPrueba+"</p>\n" +
                "        <p><span>Fecha:</span> "+fechaProximaPrueba+"</p>\n" +
                "      </div>\n" +
                "      <p>Por favor, asegúrese de que su representado(a) esté preparado(a) para esta evaluación. Si tiene alguna pregunta o desea más información, no dude en contactarnos.</p>\n" +
                "    </div>\n" +
                "    <div class=\"email-footer\">\n" +
                "      &copy; 2024 - Institución Educativa. Todos los derechos reservados.<br>\n" +
                "      <a href=\"#\">Contáctanos</a> | <a href=\"#\">Política de Privacidad</a>\n" +
                "    </div>\n" +
                "  </div>\n" +
                "</body>\n" +
                "</html>\n";
    }

    public String mensajeAprobacionPruebasIns (String nombreEstudiante, String nombrePrueba, String descripcionPrueba, String fechaAprobacion, String curso) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "  <meta charset=\"UTF-8\">\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "  <title>Notificación de Matrícula</title>\n" +
                "  <style>\n" +
                "    body {\n" +
                "      font-family: Arial, sans-serif;\n" +
                "      margin: 0;\n" +
                "      padding: 0;\n" +
                "      background-color: #f4f4f4;\n" +
                "      color: #333;\n" +
                "      line-height: 1.5;\n" +
                "    }\n" +
                "\n" +
                "    .email-container {\n" +
                "      max-width: 600px;\n" +
                "      margin: 20px auto;\n" +
                "      background: #ffffff;\n" +
                "      border: 1px solid #ddd;\n" +
                "      border-radius: 10px;\n" +
                "      box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);\n" +
                "      overflow: hidden;\n" +
                "    }\n" +
                "\n" +
                "    .email-header {\n" +
                "      background: #28a745;\n" +
                "      color: #ffffff;\n" +
                "      text-align: center;\n" +
                "      padding: 20px;\n" +
                "      font-size: 22px;\n" +
                "    }\n" +
                "\n" +
                "    .email-body {\n" +
                "      padding: 20px;\n" +
                "    }\n" +
                "\n" +
                "    .email-body h2 {\n" +
                "      margin: 0;\n" +
                "      color: #28a745;\n" +
                "    }\n" +
                "\n" +
                "    .email-body p {\n" +
                "      margin: 10px 0;\n" +
                "    }\n" +
                "\n" +
                "    .achievement, .enrollment {\n" +
                "      margin: 15px 0;\n" +
                "      padding: 15px;\n" +
                "      background: #f9f9f9;\n" +
                "      border-left: 5px solid #28a745;\n" +
                "      border-radius: 5px;\n" +
                "    }\n" +
                "\n" +
                "    .achievement span, .enrollment span {\n" +
                "      font-weight: bold;\n" +
                "      color: #333;\n" +
                "    }\n" +
                "\n" +
                "    .enrollment {\n" +
                "      border-left: 5px solid #007bff;\n" +
                "    }\n" +
                "\n" +
                "    .enrollment h3 {\n" +
                "      color: #007bff;\n" +
                "      margin-top: 0;\n" +
                "    }\n" +
                "\n" +
                "    .email-footer {\n" +
                "      background: #f4f4f4;\n" +
                "      text-align: center;\n" +
                "      padding: 10px;\n" +
                "      font-size: 14px;\n" +
                "      color: #666;\n" +
                "    }\n" +
                "\n" +
                "    .email-footer a {\n" +
                "      color: #28a745;\n" +
                "      text-decoration: none;\n" +
                "    }\n" +
                "\n" +
                "    @media (max-width: 600px) {\n" +
                "      .email-body {\n" +
                "        padding: 15px;\n" +
                "      }\n" +
                "\n" +
                "      .email-header {\n" +
                "        font-size: 20px;\n" +
                "        padding: 15px;\n" +
                "      }\n" +
                "    }\n" +
                "  </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "  <div class=\"email-container\">\n" +
                "    <div class=\"email-header\">\n" +
                "      ¡Felicidades, Matrícula Completada!\n" +
                "    </div>\n" +
                "    <div class=\"email-body\">\n" +
                "      <h2>Estimado(a) Representante de "+nombreEstudiante+"</h2>\n" +
                "      <p>Nos complace informarle que su representado(a) ha aprobado la última prueba del ciclo académico:</p>\n" +
                "      <div class=\"achievement\">\n" +
                "        <p><span>Nombre de la Prueba:</span> "+nombrePrueba+"</p>\n" +
                "        <p><span>Descripción:</span> "+descripcionPrueba +"</p>\n" +
                "        <p><span>Fecha de Aprobación:</span> "+fechaAprobacion+"</p>\n" +
                "      </div>\n" +
                "      <p>Como resultado, nos enorgullece comunicarle que su representado(a) ha sido oficialmente <span style=\"font-weight: bold; color: #28a745;\">matriculado(a)</span> en el siguiente nivel académico.</p>\n" +
                "      <div class=\"enrollment\">\n" +
                "        <h3>Información de Matrícula:</h3>\n" +
                "        <p><span>Grado/Curso:</span> "+curso+"</p>\n" +
                "      </div>\n" +
                "      <p>Por favor, asegúrese de revisar toda la información académica de su representado(a) y no dude en contactarnos si necesita alguna aclaración.</p>\n" +
                "    </div>\n" +
                "    <div class=\"email-footer\">\n" +
                "      &copy; 2024 - Institución Educativa. Todos los derechos reservados.<br>\n" +
                "      <a href=\"#\">Contáctanos</a> | <a href=\"#\">Política de Privacidad</a>\n" +
                "    </div>\n" +
                "  </div>\n" +
                "</body>\n" +
                "</html>\n";
    }

    public String mensajeActivarCuenta(String nombres, String usuario, String contrasena) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "  <meta charset=\"UTF-8\">\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "  <title>Cuenta Activada</title>\n" +
                "  <style>\n" +
                "    body {\n" +
                "      font-family: Arial, sans-serif;\n" +
                "      background-color: #f4f4f4;\n" +
                "      margin: 0;\n" +
                "      padding: 0;\n" +
                "    }\n" +
                "\n" +
                "    .email-container {\n" +
                "      width: 100%;\n" +
                "      max-width: 600px;\n" +
                "      margin: 20px auto;\n" +
                "      background-color: #ffffff;\n" +
                "      border: 1px solid #ddd;\n" +
                "      border-radius: 10px;\n" +
                "      box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);\n" +
                "      overflow: hidden;\n" +
                "    }\n" +
                "\n" +
                "    .email-header {\n" +
                "      background-color: #4CAF50;\n" +
                "      color: white;\n" +
                "      padding: 20px;\n" +
                "      text-align: center;\n" +
                "      font-size: 24px;\n" +
                "    }\n" +
                "\n" +
                "    .email-body {\n" +
                "      padding: 20px;\n" +
                "      color: #333;\n" +
                "    }\n" +
                "\n" +
                "    .email-body h2 {\n" +
                "      color: #4CAF50;\n" +
                "      font-size: 22px;\n" +
                "    }\n" +
                "\n" +
                "    .email-body p {\n" +
                "      font-size: 16px;\n" +
                "      margin-bottom: 15px;\n" +
                "    }\n" +
                "\n" +
                "    .user-info {\n" +
                "      background-color: #f9f9f9;\n" +
                "      padding: 10px;\n" +
                "      margin-top: 20px;\n" +
                "      border-radius: 5px;\n" +
                "      border-left: 5px solid #4CAF50;\n" +
                "    }\n" +
                "\n" +
                "    .user-info p {\n" +
                "      margin: 5px 0;\n" +
                "      font-size: 16px;\n" +
                "    }\n" +
                "\n" +
                "    .email-footer {\n" +
                "      background-color: #f4f4f4;\n" +
                "      padding: 15px;\n" +
                "      text-align: center;\n" +
                "      font-size: 14px;\n" +
                "      color: #888;\n" +
                "    }\n" +
                "\n" +
                "    .email-footer a {\n" +
                "      color: #4CAF50;\n" +
                "      text-decoration: none;\n" +
                "    }\n" +
                "\n" +
                "    @media (max-width: 600px) {\n" +
                "      .email-body {\n" +
                "        padding: 15px;\n" +
                "      }\n" +
                "\n" +
                "      .email-header {\n" +
                "        font-size: 20px;\n" +
                "        padding: 15px;\n" +
                "      }\n" +
                "    }\n" +
                "  </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "  <div class=\"email-container\">\n" +
                "    <div class=\"email-header\">\n" +
                "      Tu Cuenta Ha Sido Activada\n" +
                "    </div>\n" +
                "    <div class=\"email-body\">\n" +
                "      <h2>¡Hola "+nombres+"!</h2>\n" +
                "      <p>Nos complace informarte que tu cuenta ha sido activada de nuevo. Ahora puedes acceder a tu cuenta utilizando las siguientes credenciales:</p>\n" +
                "      \n" +
                "      <div class=\"user-info\">\n" +
                "        <p><strong>Nombre de Usuario:</strong> "+usuario+"</p>\n" +
                "        <p><strong>Contraseña:</strong> "+contrasena+"</p>\n" +
                "      </div>\n" +
                "      \n" +
                "      <p>Si no has solicitado esta activación o tienes alguna duda, por favor, contacta con nuestro soporte.</p>\n" +
                "      <p>Gracias por ser parte de nuestra comunidad.</p>\n" +
                "    </div>\n" +
                "    <div class=\"email-footer\">\n" +
                "      &copy; 2024 - Tu Empresa. Todos los derechos reservados.<br>\n" +
                "      <a href=\"#\">Contáctanos</a> | <a href=\"#\">Política de Privacidad</a>\n" +
                "    </div>\n" +
                "  </div>\n" +
                "</body>\n" +
                "</html>\n";
    }
}

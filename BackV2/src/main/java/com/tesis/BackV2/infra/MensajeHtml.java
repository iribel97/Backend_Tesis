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

    public String mensajeEntregaTardeAsignacion(String nombreRepresentante, String nombreEstudiante, String nombreTarea, String fechaEntrega, String fechaEntregaTarde) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "  <meta charset=\"UTF-8\">\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "  <title>Entrega Tardía</title>\n" +
                "  <style>\n" +
                "    body {\n" +
                "      font-family: Arial, sans-serif;\n" +
                "      background-color: #f4f4f4;\n" +
                "      margin: 0;\n" +
                "      padding: 0;\n" +
                "    }\n" +
                "    .email-container {\n" +
                "      max-width: 600px;\n" +
                "      margin: 30px auto;\n" +
                "      background-color: #ffffff;\n" +
                "      border-radius: 10px;\n" +
                "      box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);\n" +
                "      overflow: hidden;\n" +
                "      border: 1px solid #e6e6e6;\n" +
                "    }\n" +
                "    .header {\n" +
                "      background-color: #d9534f;\n" +
                "      color: #ffffff;\n" +
                "      padding: 20px;\n" +
                "      text-align: center;\n" +
                "    }\n" +
                "    .header h1 {\n" +
                "      margin: 0;\n" +
                "      font-size: 24px;\n" +
                "    }\n" +
                "    .content {\n" +
                "      padding: 20px;\n" +
                "      color: #333333;\n" +
                "      line-height: 1.6;\n" +
                "    }\n" +
                "    .content p {\n" +
                "      margin: 0 0 10px;\n" +
                "    }\n" +
                "    .content .highlight {\n" +
                "      background-color: #ffeeba;\n" +
                "      padding: 10px;\n" +
                "      border-radius: 5px;\n" +
                "      color: #856404;\n" +
                "      font-weight: bold;\n" +
                "    }\n" +
                "    .footer {\n" +
                "      background-color: #f8f9fa;\n" +
                "      text-align: center;\n" +
                "      padding: 15px;\n" +
                "      font-size: 12px;\n" +
                "      color: #6c757d;\n" +
                "    }\n" +
                "    .footer a {\n" +
                "      color: #d9534f;\n" +
                "      text-decoration: none;\n" +
                "    }\n" +
                "  </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "  <div class=\"email-container\">\n" +
                "    <div class=\"header\">\n" +
                "      <h1>Entrega Tardía de Tarea</h1>\n" +
                "    </div>\n" +
                "    <div class=\"content\">\n" +
                "      <p>Estimado/a <strong>"+nombreRepresentante+"</strong>,</p>\n" +
                "      <p>Le informamos que su representado/a <strong>"+nombreEstudiante+"</strong> ha entregado la siguiente tarea fuera del tiempo establecido:</p>\n" +
                "      <div class=\"highlight\">\n" +
                "        <p><strong>Tarea:</strong> "+nombreTarea+"</p>\n" +
                "        <p><strong>Fecha de Entrega Programada:</strong> "+fechaEntrega+"</p>\n" +
                "        <p><strong>Fecha de Entrega Real:</strong> "+fechaEntregaTarde+"</p>\n" +
                "      </div>\n" +
                "      <p>Le recordamos la importancia de cumplir con los plazos establecidos para mantener un buen desempeño académico.</p>\n" +
                "      <p>Si tiene alguna consulta, no dude en comunicarse con nosotros.</p>\n" +
                "    </div>\n" +
                "    <div class=\"footer\">\n" +
                "      <p>Este es un mensaje automático, por favor no responda a este correo.</p>\n" +
                "      <p>Para más información, visite nuestro <a href=\"#\">sitio web</a>.</p>\n" +
                "    </div>\n" +
                "  </div>\n" +
                "</body>\n" +
                "</html>\n";
    }

    public String mensajeBajaNotaEntrega(String nombreEstudiante, String nombreMateria, String nombreEvaluacion, String nota, String notaMinima) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Notificación de Baja Nota</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: Arial, sans-serif;\n" +
                "            background-color: #f9f9f9;\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "        }\n" +
                "\n" +
                "        .email-container {\n" +
                "            max-width: 600px;\n" +
                "            margin: 20px auto;\n" +
                "            background-color: #ffffff;\n" +
                "            border-radius: 8px;\n" +
                "            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);\n" +
                "            overflow: hidden;\n" +
                "        }\n" +
                "\n" +
                "        .header {\n" +
                "            background-color: #ff4d4d;\n" +
                "            color: #ffffff;\n" +
                "            text-align: center;\n" +
                "            padding: 20px;\n" +
                "        }\n" +
                "\n" +
                "        .header h1 {\n" +
                "            margin: 0;\n" +
                "            font-size: 24px;\n" +
                "        }\n" +
                "\n" +
                "        .content {\n" +
                "            padding: 20px;\n" +
                "            color: #333333;\n" +
                "            line-height: 1.6;\n" +
                "        }\n" +
                "\n" +
                "        .content p {\n" +
                "            margin: 10px 0;\n" +
                "        }\n" +
                "\n" +
                "        .highlight {\n" +
                "            background-color: #ffe6e6;\n" +
                "            border-left: 4px solid #ff4d4d;\n" +
                "            padding: 10px;\n" +
                "            margin: 20px 0;\n" +
                "            border-radius: 4px;\n" +
                "        }\n" +
                "\n" +
                "        .footer {\n" +
                "            background-color: #f1f1f1;\n" +
                "            color: #555555;\n" +
                "            text-align: center;\n" +
                "            padding: 15px;\n" +
                "            font-size: 14px;\n" +
                "        }\n" +
                "\n" +
                "        .footer a {\n" +
                "            color: #ff4d4d;\n" +
                "            text-decoration: none;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"email-container\">\n" +
                "        <div class=\"header\">\n" +
                "            <h1>Notificación de Baja Nota</h1>\n" +
                "        </div>\n" +
                "\n" +
                "        <div class=\"content\">\n" +
                "            <p>Estimado/a representante:</p>\n" +
                "\n" +
                "            <p>Le informamos que el estudiante <strong>"+nombreEstudiante+"</strong> ha obtenido una calificación baja en una de sus evaluaciones recientes. Detalles de la evaluación:</p>\n" +
                "\n" +
                "            <div class=\"highlight\">\n" +
                "                <p><strong>Materia:</strong> "+nombreMateria+"</p>\n" +
                "                <p><strong>Nombre de la Asignación:</strong> "+nombreEvaluacion+"</p>\n" +
                "                <p><strong>Calificación Obtenida:</strong> "+nota+"</p>\n" +
                "                <p><strong>Calificación Mínima Aprobatoria:</strong> "+notaMinima+"</p>\n" +
                "            </div>\n" +
                "\n" +
                "            <p>Es importante apoyar al estudiante para mejorar su desempeño académico. Si tiene alguna duda o desea coordinar una reunión, no dude en contactarnos.</p>\n" +
                "\n" +
                "            <p>Atentamente,</p>\n" +
                "            <p><strong>[Nombre de la Institución]</strong></p>\n" +
                "        </div>\n" +
                "\n" +
                "        <div class=\"footer\">\n" +
                "            <p>Este es un correo generado automáticamente. Si tiene preguntas, <a href=\"mailto:soporte@institucion.com\">contáctenos aquí</a>.</p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>\n";
    }

    public String mensajeAprobacionIns(String nombreRepresentante, String nombreEstudiante, String gradoCurso, String fechaInicioClases) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: Arial, sans-serif;\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "            background-color: #f7f7f7;\n" +
                "            color: #333;\n" +
                "        }\n" +
                "\n" +
                "        .email-container {\n" +
                "            max-width: 600px;\n" +
                "            margin: 20px auto;\n" +
                "            background-color: #ffffff;\n" +
                "            border-radius: 8px;\n" +
                "            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);\n" +
                "            overflow: hidden;\n" +
                "        }\n" +
                "\n" +
                "        .email-header {\n" +
                "            background-color: #4CAF50;\n" +
                "            color: #ffffff;\n" +
                "            padding: 20px;\n" +
                "            text-align: center;\n" +
                "        }\n" +
                "\n" +
                "        .email-header h1 {\n" +
                "            margin: 0;\n" +
                "            font-size: 24px;\n" +
                "        }\n" +
                "\n" +
                "        .email-body {\n" +
                "            padding: 20px;\n" +
                "        }\n" +
                "\n" +
                "        .email-body p {\n" +
                "            margin: 15px 0;\n" +
                "            line-height: 1.6;\n" +
                "        }\n" +
                "\n" +
                "        .email-body .highlight {\n" +
                "            background-color: #e7f7e7;\n" +
                "            padding: 10px;\n" +
                "            border-left: 4px solid #4CAF50;\n" +
                "            margin: 20px 0;\n" +
                "        }\n" +
                "\n" +
                "        .email-footer {\n" +
                "            background-color: #f1f1f1;\n" +
                "            color: #666;\n" +
                "            text-align: center;\n" +
                "            padding: 15px;\n" +
                "            font-size: 14px;\n" +
                "        }\n" +
                "\n" +
                "        .email-footer a {\n" +
                "            color: #4CAF50;\n" +
                "            text-decoration: none;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "    <div class=\"email-container\">\n" +
                "        <div class=\"email-header\">\n" +
                "            <h1>¡Inscripción Aceptada!</h1>\n" +
                "        </div>\n" +
                "        <div class=\"email-body\">\n" +
                "            <p>Estimado/a <strong>"+nombreRepresentante+"</strong>,</p>\n" +
                "            <p>Nos complace informarle que su representado/a, <strong>"+nombreEstudiante+"</strong>, ha sido inscrito\n" +
                "                y matriculado exitosamente.</p>\n" +
                "            <div class=\"highlight\">\n" +
                "                <h3>Información de Matrícula:</h3>\n" +
                "        <p><span>Grado/Curso:</span> "+gradoCurso+"</p>\n" +
                "        <p><span>Inicio de Clases:</span> "+fechaInicioClases+"</p>\n" +
                "            </div>\n" +
                "            <p>Estamos emocionados de dar la bienvenida a su representado/a y agradecemos la confianza depositada en\n" +
                "                nuestra institución.</p>\n" +
                "            <p>Si tiene alguna consulta, no dude en comunicarse con nosotros.</p>\n" +
                "        </div>\n" +
                "        <div class=\"email-footer\">\n" +
                "            <p>&copy; [Año de la Institución] [Nombre de la Institución]. Todos los derechos reservados.</p>\n" +
                "            <p><a href=\"mailto:contacto@institucion.com\">contacto@institucion.com</a> | Teléfono: [Número de Teléfono]\n" +
                "            </p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "\n" +
                "</html>";
    }

    public String mensajeCitacionDocenteRep (String nombreRepresentante, String nombreEstudiante, String nombreDocente, String fechaCitacion, String horaCitacion, String motivoCitacion, String observaciones) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Citación</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: 'Arial', sans-serif;\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "            background-color: #f4f4f4;\n" +
                "        }\n" +
                "        .email-container {\n" +
                "            max-width: 600px;\n" +
                "            margin: 20px auto;\n" +
                "            background-color: #ffffff;\n" +
                "            border-radius: 10px;\n" +
                "            overflow: hidden;\n" +
                "            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);\n" +
                "        }\n" +
                "        .email-header {\n" +
                "            background: linear-gradient(90deg, #007bff, #0056b3);\n" +
                "            color: #ffffff;\n" +
                "            padding: 20px;\n" +
                "            text-align: center;\n" +
                "        }\n" +
                "        .email-header h1 {\n" +
                "            margin: 0;\n" +
                "            font-size: 26px;\n" +
                "            font-weight: bold;\n" +
                "        }\n" +
                "        .email-body {\n" +
                "            padding: 20px 30px;\n" +
                "            color: #333333;\n" +
                "        }\n" +
                "        .email-body p {\n" +
                "            margin: 15px 0;\n" +
                "            line-height: 1.6;\n" +
                "            font-size: 16px;\n" +
                "        }\n" +
                "        .details {\n" +
                "            margin: 20px 0;\n" +
                "            padding: 15px;\n" +
                "            background-color: #f9f9f9;\n" +
                "            border: 1px solid #e0e0e0;\n" +
                "            border-radius: 8px;\n" +
                "        }\n" +
                "        .details .detail-item {\n" +
                "            margin-bottom: 10px;\n" +
                "            font-size: 16px;\n" +
                "        }\n" +
                "        .details .detail-item strong {\n" +
                "            color: #007bff;\n" +
                "        }\n" +
                "        .button-container {\n" +
                "            text-align: center;\n" +
                "            margin: 20px 0;\n" +
                "        }\n" +
                "        .button {\n" +
                "            display: inline-block;\n" +
                "            padding: 12px 25px;\n" +
                "            background-color: #007bff;\n" +
                "            color: #ffffff;\n" +
                "            text-decoration: none;\n" +
                "            font-weight: bold;\n" +
                "            border-radius: 5px;\n" +
                "            font-size: 16px;\n" +
                "            transition: background-color 0.3s;\n" +
                "        }\n" +
                "        .button:hover {\n" +
                "            background-color: #0056b3;\n" +
                "        }\n" +
                "        .email-footer {\n" +
                "            background-color: #f8f9fa;\n" +
                "            padding: 15px;\n" +
                "            text-align: center;\n" +
                "            font-size: 14px;\n" +
                "            color: #888888;\n" +
                "            border-top: 1px solid #eeeeee;\n" +
                "        }\n" +
                "        .email-footer p {\n" +
                "            margin: 5px 0;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"email-container\">\n" +
                "        <!-- Header -->\n" +
                "        <div class=\"email-header\">\n" +
                "            <h1>Citación Importante</h1>\n" +
                "        </div>\n" +
                "        <!-- Body -->\n" +
                "        <div class=\"email-body\">\n" +
                "            <p>Estimado/a <strong>" + nombreRepresentante + "</strong>,</p>\n" +
                "            <p>\n" +
                "                Le informamos que ha sido programada una citación para tratar asuntos relacionados con el estudiante \n" +
                "                <strong>"+ nombreEstudiante +"</strong>.\n" +
                "            </p>\n" +
                "            <div class=\"details\">\n" +
                "                <div class=\"detail-item\"><strong>Docente:</strong>" + nombreDocente + "</div>\n" +
                "                <div class=\"detail-item\"><strong>Motivo:</strong> "+motivoCitacion+"</div>\n" +
                "                <div class=\"detail-item\"><strong>Fecha:</strong> "+fechaCitacion+"</div>\n" +
                "                <div class=\"detail-item\"><strong>Hora:</strong> "+horaCitacion+"</div>\n" +
                "                <div class=\"detail-item\"><strong>Observaciones:</strong> "+observaciones+"</div>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "        <!-- Footer -->\n" +
                "        <div class=\"email-footer\">\n" +
                "            <p>Este es un mensaje automático, por favor no responder a este correo.</p>\n" +
                "            <p>&copy; 2025 Sistema Académico. Todos los derechos reservados.</p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>\n";
    }

    public String mensajeFaltaClases(String nombreEstudiante, String materia, String fecha) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Notificación de Falta de Asistencia</title>\n" +
                "    <style>\n" +
                "        body { font-family: Arial, sans-serif; background-color: #f9f9f9; margin: 0; padding: 0; }\n" +
                "        .email-container { max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); overflow: hidden; }\n" +
                "        .header { background-color: #d9534f; color: #ffffff; text-align: center; padding: 20px; }\n" +
                "        .header h1 { margin: 0; font-size: 24px; }\n" +
                "        .content { padding: 20px; color: #333; line-height: 1.6; }\n" +
                "        .highlight { background-color: #ffe6e6; border-left: 4px solid #d9534f; padding: 10px; margin: 20px 0; border-radius: 4px; }\n" +
                "        .footer { background-color: #f1f1f1; color: #555; text-align: center; padding: 15px; font-size: 14px; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"email-container\">\n" +
                "        <div class=\"header\">\n" +
                "            <h1>Notificación de Falta de Asistencia</h1>\n" +
                "        </div>\n" +
                "        <div class=\"content\">\n" +
                "            <p>Estimado/a representante,</p>\n" +
                "            <p>Le notificamos que su representado <strong>" + nombreEstudiante + "</strong> ha registrado una falta de asistencia:</p>\n" +
                "            <div class=\"highlight\">\n" +
                "                <p><strong>Materia:</strong> " + materia + "</p>\n" +
                "                <p><strong>Fecha:</strong> " + fecha + "</p>\n" +
                "            </div>\n" +
                "            <p>Le recomendamos tomar las medidas necesarias para evitar futuras ausencias.</p>\n" +
                "        </div>\n" +
                "        <div class=\"footer\">\n" +
                "            <p>Este es un mensaje automático. No responda a este correo.</p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }

}

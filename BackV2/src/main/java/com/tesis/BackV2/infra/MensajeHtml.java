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
}

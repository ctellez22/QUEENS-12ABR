package logica;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Impresora {

    public void imprimirEtiqueta(String zplData) {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            imprimirEnWindows(zplData);
        } else if (osName.contains("mac")) {
            imprimirEnMac(zplData);
        } else {
            System.err.println("Sistema operativo no soportado para la impresión.");
        }
    }

    private void imprimirEnWindows(String zplData) {
        String filePath = System.getProperty("user.home") + "\\Desktop\\bebeBoste.zpl";
        String printerName = "ZDesigner ZD421-300dpi ZPL";
        String rawPrintDirectory = "C:\\Users\\ASUS\\OneDrive\\Escritorio";

        try {
            // Crear el archivo ZPL
            File zplFile = new File(filePath);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(zplFile, StandardCharsets.UTF_8))) {
                writer.write(zplData);
            }

            System.out.println("Archivo ZPL creado exitosamente en: " + zplFile.getAbsolutePath());

            // Crear el comando para enviar el archivo ZPL a la impresora
            String rawPrintCommand = String.format("RawPrint /f \"%s\" /pr \"%s\"", zplFile.getAbsolutePath(), printerName);
            ProcessBuilder rawPrintProcessBuilder = new ProcessBuilder("cmd", "/c", rawPrintCommand);
            rawPrintProcessBuilder.directory(new File(rawPrintDirectory));
            rawPrintProcessBuilder.redirectOutput(ProcessBuilder.Redirect.DISCARD);
            rawPrintProcessBuilder.redirectError(ProcessBuilder.Redirect.DISCARD);
            Process rawPrintProcess = rawPrintProcessBuilder.start();

            // Esperar a que termine la impresión
            int printExitCode = rawPrintProcess.waitFor();
            if (printExitCode == 0) {
                System.out.println("Etiqueta enviada a la impresora exitosamente.");
            } else {
                System.err.println("Error al enviar la etiqueta a la impresora. Código de salida: " + printExitCode);
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error al crear el archivo ZPL o al encontrar RawPrint.exe.");
            e.printStackTrace();
        }
    }

    private void imprimirEnMac(String zplData) {
        String filePath = System.getProperty("user.home") + "/Desktop/bebeBoste.zpl";
        String printerName = "Zebra_Technologies_ZTC_ZD421-203dpi_ZPL"; // Cambiar por el nombre exacto de tu impresora en macOS

        try {
            // Crear el archivo ZPL
            File zplFile = new File(filePath);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(zplFile, StandardCharsets.UTF_8))) {
                writer.write(zplData);
            }

            System.out.println("Archivo ZPL creado exitosamente en: " + zplFile.getAbsolutePath());

            // Crear el comando para enviar el archivo ZPL en modo raw a la impresora
            String lpCommand = String.format("lp -d \"%s\" -o raw \"%s\"", printerName, zplFile.getAbsolutePath());

            // Configurar el proceso para ejecutar el comando
            ProcessBuilder lpProcessBuilder = new ProcessBuilder("bash", "-c", lpCommand);

            // Redirigir salida estándar y errores para mayor claridad
            lpProcessBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            lpProcessBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);

            // Iniciar el proceso
            Process lpProcess = lpProcessBuilder.start();

            // Esperar a que termine la impresión
            int printExitCode = lpProcess.waitFor();
            if (printExitCode == 0) {
                System.out.println("Etiqueta enviada a la impresora exitosamente.");
            } else {
                System.err.println("Error al enviar la etiqueta a la impresora. Código de salida: " + printExitCode);
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error al crear el archivo ZPL o al enviar la etiqueta a la impresora.");
            e.printStackTrace();
        }
    }
}

package appjpm4everyone.pdf_muestra;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import harmony.java.awt.Color;


public class MainActivity extends AppCompatActivity  {

    private final static String NOMBRE_DIRECTORIO = "MiPdf";
    private final static String NOMBRE_DOCUMENTO = "PDFMUESTRA.pdf";
    private final static String ETIQUETA_ERROR = "ERROR PDF";
    Context contexto;
    //Variable para el archivo PDF
    private File archivoPDF;
    //Bandera que me indica si existe el archivo PDF
    private int fPDF = 0;

    //Botones
    Button btn1, btn2, btn3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contexto = getApplicationContext();

        // Generaremos el documento al hacer click sobre el botón.
        btn1 = (Button) findViewById(R.id.btnPDFGENERAR);
        btn2 = (Button) findViewById(R.id.btnPDFLOCAL);
        btn3 = (Button) findViewById(R.id.btnPDFEXT);



    }

    public void ClickPDF(View view) {

        //Por medio de un switch case
        switch (view.getId()){
            case R.id.btnPDFGENERAR: crearPDF();
                break;
            case R.id.btnPDFLOCAL:   verPDF();
                break;
            case R.id.btnPDFEXT:     verPDFAPP(this);  //Pongo el parametro this, porque envio el inicio de una actividad
                break;

        }//Final switch  case

    }



    private void verPDFAPP(Activity activity) {
        //Primero busca el archivo
        buscarPDF();
        if(fPDF==1) {
            Toast.makeText(contexto,"Si existe archivo PDF para LEER",Toast.LENGTH_LONG).show();
            Uri uri= Uri.fromFile(archivoPDF);
            //Creo un Intent para inciar una nueva actividad, pero de otra APP
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "applicacion/PDF");
            //Try catch ´por si no existe una APP en el dispositivo
            try{
                activity.startActivity(intent);
            }catch (ActivityNotFoundException e){
                activity.startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=cn.wps.moffice_eng&hl=es")));
                //Mensaje en un Toast para que se visualize el posible error
                Toast.makeText(activity.getApplicationContext(),
                        "No hay una APP para ver PDFs", Toast.LENGTH_LONG).show();
                Log.e("verPDFAPP",e.toString());
            }

        }
        else
        if(fPDF==0){
            Toast.makeText(contexto,"No existe archivo PDF para LEER",Toast.LENGTH_LONG).show();
        }


    }


    private void verPDF() {
            //Primero busca el archivo
            buscarPDF();
            if(fPDF==1) {
                //De que actividad estoy a cual quiero ir
                Intent intent = new Intent(contexto, VisorPDF.class);
                //Envío de información entre actividades
                intent.putExtra("path", archivoPDF.getAbsolutePath());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                contexto.startActivity(intent);
                Toast.makeText(contexto,"Si existe archivo PDF para LEER",Toast.LENGTH_LONG).show();
            }
            else
            if(fPDF==0){
                Toast.makeText(contexto,"No existe archivo PDF para LEER",Toast.LENGTH_LONG).show();
            }
    }

    private void buscarPDF() {
        try{
        File carpeta = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            carpeta = new File(
                    Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    NOMBRE_DIRECTORIO);
            archivoPDF = new File(carpeta, NOMBRE_DOCUMENTO);


            //Verifica si la carpeta existe
            if (archivoPDF.exists()) {
                fPDF = 1;     //La bandera esta activada para leer PDFs
            } else {
                fPDF = 0;     //La bandera está desactivada para leer los PDFs
            }
        }
        }catch (Exception e){
            //Mensaje que me dirá si hay errores
            Log.e(ETIQUETA_ERROR,e.toString());
        }

    }


    private void crearPDF() {

        // Creamos el documento.
        Document documento = new Document();

        try {

            //Tomo la fecha y la hora del sistema (Telefono, Tablet, SmartWatch, etc)
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();

            // Creamos el fichero con el nombre que deseemos.
            archivoPDF = crearFichero(NOMBRE_DOCUMENTO);

            // Creamos el flujo de datos de salida para el fichero donde
            // guardaremos el pdf.
            FileOutputStream ficheroPdf = new FileOutputStream(
                    archivoPDF.getAbsolutePath());

            // Asociamos el flujo que acabamos de crear al documento.
            PdfWriter writer = PdfWriter.getInstance(documento, ficheroPdf);

            // Incluimos el píe de página y una cabecera
            HeaderFooter cabecera = new HeaderFooter(new Phrase(
                    "Esta es mi cabecera"), false);
            HeaderFooter pie = new HeaderFooter(new Phrase(
                    "Este es mi pie de página  " + date), false);


            documento.setHeader(cabecera);
            documento.setFooter(pie);

            // Abrimos el documento.
            documento.open();

            // Añadimos un título con la fuente por defecto.
            documento.add(new Paragraph("Título 1"));

            // Añadimos un título con una fuente personalizada.
            Font font = FontFactory.getFont(FontFactory.HELVETICA, 28,
                    Font.BOLD, Color.RED);
            documento.add(new Paragraph("Título personalizado", font));

            // Insertamos una imagen que se encuentra en los recursos de la
            // aplicación.
            Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.jillvalentine);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image imagen = Image.getInstance(stream.toByteArray());
            documento.add(imagen);

            // Insertamos una tabla.
            PdfPTable tabla = new PdfPTable(5);
            for (int i = 0; i < 15; i++) {
                tabla.addCell("Celda " + i);
            }
            documento.add(tabla);

            // Agregar marca de agua
            font = FontFactory.getFont(FontFactory.HELVETICA, 42, Font.BOLD,
                    Color.GRAY);
            ColumnText.showTextAligned(writer.getDirectContentUnder(),
                    Element.ALIGN_CENTER, new Paragraph(
                            "appJPM4Everyone", font), 297.5f, 421,
                    writer.getPageNumber() % 2 == 1 ? 45 : -45);
            Toast.makeText(contexto,"PDF creado correctamente",Toast.LENGTH_LONG).show();

        } catch (DocumentException e) {

            Log.e(ETIQUETA_ERROR, e.getMessage());

        } catch (IOException e) {

            Log.e(ETIQUETA_ERROR, e.getMessage());


        } finally {

            // Cerramos el documento.
            documento.close();
        }

    }



    /**
         * Crea un fichero con el nombre que se le pasa a la función y en la ruta
         * especificada.
         *
         * @param nombreFichero
         * @return
         * @throws IOException
         */
    public static File crearFichero(String nombreFichero) throws IOException {
        File ruta = getRuta();
        File fichero = null;
        if (ruta != null)
            fichero = new File(ruta, nombreFichero);
        return fichero;
    }

    /**
     * Obtenemos la ruta donde vamos a almacenar el fichero.
     *
     * @return
     */
    public static File getRuta() {

        // El fichero será almacenado en un directorio dentro del directorio
        // Descargas
        File ruta = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            ruta = new File(
                    Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    NOMBRE_DIRECTORIO);

            if (ruta != null) {
                if (!ruta.mkdirs()) {
                    if (!ruta.exists()) {
                        return null;
                    }
                }
            }
        } else {
        }

        return ruta;
    }


}







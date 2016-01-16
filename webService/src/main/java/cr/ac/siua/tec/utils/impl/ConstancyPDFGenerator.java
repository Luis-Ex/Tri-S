package cr.ac.siua.tec.utils.impl;

import cr.ac.siua.tec.utils.PDFGenerator;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Component("Constancy")
@PropertySource(value={"classpath:application-data.properties"})
public class ConstancyPDFGenerator extends PDFGenerator {

    @Value("${coordinator.name}")
    private String coordinatorName;

    @Override
    public String generate(HashMap<String, String> formValues) {
        String templateName = "constancia.pdf";
        String pdfPath = PDFGenerator.RESOURCES_PATH + templateName;
        return populateAndCopy(pdfPath, formValues);
    }

    private Map<Integer, String> monthsMap = new HashMap<>();

    private void initMonthsMap() {
        monthsMap.put(0, "enero");
        monthsMap.put(1, "febrero");
        monthsMap.put(2, "marzo");
        monthsMap.put(3, "abril");
        monthsMap.put(4, "mayo");
        monthsMap.put(5, "junio");
        monthsMap.put(6, "julio");
        monthsMap.put(7, "agosto");
        monthsMap.put(8, "setiembre");
        monthsMap.put(9, "octubre");
        monthsMap.put(10, "noviembre");
        monthsMap.put(11, "diciembre");
    }

    private String populateAndCopy(String originalPdf, HashMap<String, String> formValues) {
        try {
            PDDocument _pdfDocument = PDDocument.load(originalPdf);
            PDDocumentCatalog docCatalog = _pdfDocument.getDocumentCatalog();
            PDAcroForm acroForm = docCatalog.getAcroForm();

            String coordinator = new String(coordinatorName.getBytes("ISO-8859-1"), "UTF-8");
            acroForm.getField("Coordinador1").setValue(coordinator);
            acroForm.getField("Coordinador2").setValue(coordinator);

            initMonthsMap();
            Calendar cal = Calendar.getInstance();
            int day = cal.get(Calendar.DAY_OF_MONTH);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            String date = String.valueOf(day) + " de " + monthsMap.get(month) + " del año " + String.valueOf(year) + ".";
            acroForm.getField("Fecha").setValue(date);

            formValues.remove("Queue");
            formValues.remove("Motivo");
            formValues.remove("Requestors");

            for(Map.Entry<String, String> entry : formValues.entrySet()) {
                acroForm.getField(entry.getKey()).setValue(entry.getValue());
            }
            return encodePDF(_pdfDocument);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "1";
    }
}

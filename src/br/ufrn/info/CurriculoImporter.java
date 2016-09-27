package br.ufrn.info;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;

/**
 * @author Leonardo René (leonardo@info.ufrn.br)
 * @since 23/09/2016
 */
public class CurriculoImporter implements Runnable {

    final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    private TimeMetric metric;
    WSCurriculoService wsCurriculoService;
    private String cpf;

    CurriculoImporter(String cpf){
        this.cpf = cpf;
        this.wsCurriculoService = new WSCurriculoService();
        this.metric = TimeMetric.getInstance();
    }

    public void run() {
        // 1. Obtém o identificador CNPq associado ao CPF. Necessário para as demais consultas
        String id = getIdentificadorCNPq(cpf);
        // 2. Obtém a data da última atualização do currículo na plataforma Lattes. Utilizado na verificação se deve ou não fazer o download do currículo
        String data = getDataAtualizacaoCV(id);
        // 3. Obtém o arquivo zipado do currículo. Obs.: está apenas baixando sem gravar em disco a fim de contabilizar somente o tempo de download.
        byte[] arquivo = getCurriculoCompactado(id);
        File file = new File(""+cpf+".zip");
        try {
            FileOutputStream stream = new FileOutputStream(file);
            stream.write(arquivo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private String getIdentificadorCNPq(String cpf){
        long start = System.currentTimeMillis();
        String identificadorCNPq = wsCurriculoService.getWSCurriculoPort().getIdentificadorCNPq(cpf, "", "");
        //System.out.println(String.format("Identificador [%s] obtido a partir do CPF [%s]", identificadorCNPq, cpf));
        metric.addTime(Operations.GET_ID_CNPQ, System.currentTimeMillis() - start);
        return identificadorCNPq;
    }

    private String getDataAtualizacaoCV(String id) {
        long start = System.currentTimeMillis();
        String data = wsCurriculoService.getWSCurriculoPort().getDataAtualizacaoCV(id);
        //System.out.println("Data de atualização do CV ["+ id +"]: " + data);
        metric.addTime(Operations.GET_DATA_ATUALIZACAO, System.currentTimeMillis() - start);
        return data;
    }

    private byte[] getCurriculoCompactado(String id) {
        long start = System.currentTimeMillis();
        byte[] zip = wsCurriculoService.getWSCurriculoPort().getCurriculoCompactado(id);
        //System.out.println("Tamanho do arquivo do CV ["+ id +"]: " + zip.length + " bytes");
        metric.addTime(Operations.DOWNLOAD_CURRICULO_ZIP, System.currentTimeMillis() - start);
        return zip;
    }
}

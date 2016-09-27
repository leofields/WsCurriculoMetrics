package br.ufrn.info;

import java.io.FileReader;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws Exception {
        System.setOut(new PrintStream("logsaida.txt"));
        System.out.println("Iniciando importação de currículos...");
        long start = System.currentTimeMillis();
        int threadPoolSize = 20;
        Scanner scanner = new Scanner(new FileReader("CPFs.txt")).useDelimiter("\\n");

        // Pool de threads com número fixo de threads e fila ilimitada
        ExecutorService tpes = Executors.newFixedThreadPool(threadPoolSize);

        while (scanner.hasNext()) {
            String cpf = scanner.next();
            tpes.execute(new CurriculoImporter(cpf));
        }

        System.out.println(
                String.format("Tempo total para obter identificadores CNPq (HH:mm:ss): %03d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(TimeMetric.getInstance().getTime(Operations.GET_ID_CNPQ)),
                        TimeUnit.MILLISECONDS.toMinutes(TimeMetric.getInstance().getTime(Operations.GET_ID_CNPQ)) % 60,
                        TimeUnit.MILLISECONDS.toSeconds(TimeMetric.getInstance().getTime(Operations.GET_ID_CNPQ)) % 60)
        );
        System.out.println(
                String.format("Tempo total para verificar datas de atualização (HH:mm:ss): %03d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(TimeMetric.getInstance().getTime(Operations.GET_DATA_ATUALIZACAO)),
                        TimeUnit.MILLISECONDS.toMinutes(TimeMetric.getInstance().getTime(Operations.GET_DATA_ATUALIZACAO)) % 60,
                        TimeUnit.MILLISECONDS.toSeconds(TimeMetric.getInstance().getTime(Operations.GET_DATA_ATUALIZACAO)) % 60)
        );
        System.out.println(
                String.format("Tempo total de downloads (sem escrever no disco) (HH:mm:ss): %03d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(TimeMetric.getInstance().getTime(Operations.DOWNLOAD_CURRICULO_ZIP)),
                        TimeUnit.MILLISECONDS.toMinutes(TimeMetric.getInstance().getTime(Operations.DOWNLOAD_CURRICULO_ZIP)) % 60,
                        TimeUnit.MILLISECONDS.toSeconds(TimeMetric.getInstance().getTime(Operations.DOWNLOAD_CURRICULO_ZIP)) % 60)
        );

        tpes.shutdown();
        long finalTime = System.currentTimeMillis() - start;
        System.out.println(
                String.format("Tempo total de execução (HH:mm:ss): %03d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(finalTime),
                        TimeUnit.MILLISECONDS.toMinutes(finalTime) % 60,
                        TimeUnit.MILLISECONDS.toSeconds(finalTime) % 60)
        );
        System.exit(0);
    }
}

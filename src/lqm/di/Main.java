package lqm.di;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {

        ExecutorService pool = Executors.newSingleThreadExecutor();

        ProcessBuilder processBuilder = new ProcessBuilder();
        ProcessBuilder processBuilderDos = new ProcessBuilder();
        ProcessBuilder processBuilderTres = new ProcessBuilder();

        // Run this on Windows, cmd, /c = terminate after this run
        processBuilder.command("powershell", "/c", "ping google.com");
        processBuilderDos.command("powershell", "/c", "ping movistar.com");
        processBuilderTres.command("powershell", "/c", "ping as.com");

        try {

            Process process = processBuilder.start();
            Process process2 = processBuilderDos.start();
            Process process3 = processBuilderTres.start();

            ProcessReadTask task = new ProcessReadTask(process.getInputStream());
            //esperamos porque recibimos una promesa,es asincrono, le decimos
            //que ejecute una tarea como hilo
            Future<List<String>> future = pool.submit(task);
            //no bloqueo, se puede usar otras tareas aqui
            System.out.println("Nueva tarea 1...");
            System.out.println("Nueva tarea 2...");

            //en la web pone que el future nos devuelve un get algo, investigar esta parte
            future.get();

            ProcessReadTask task2 = new ProcessReadTask(process2.getInputStream());

            //esperamos porque recibimos una promesa,es asincrono, le decimos
            //que ejecute una tarea como hilo
            Future<List<String>> future2 = pool.submit(task2);
            //no bloqueo, se puede usar otras tareas aqui
            System.out.println("Nueva tarea 1...");
            System.out.println("Nueva tarea 2...");
            ProcessReadTask task3 = new ProcessReadTask(process3.getInputStream());

            //esperamos porque recibimos una promesa,es asincrono, le decimos
            //que ejecute una tarea como hilo
            Future<List<String>> future3 = pool.submit(task3);
            //no bloqueo, se puede usar otras tareas aqui
            System.out.println("Nueva tarea 1...");
            System.out.println("Nueva tarea 2...");

            //esperamos que se cumpla la tarea en x segundos

            List<String> result =future.get(10, TimeUnit.SECONDS);
            result.forEach(System.out::println);

            List<String> result2 =future2.get(10, TimeUnit.SECONDS);
            result2.forEach(System.out::println);

            List<String> result3 =future3.get(10, TimeUnit.SECONDS);
            result3.forEach(System.out::println);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } finally {
            pool.shutdown();
        }

    }


private static class ProcessReadTask implements Callable<List<String>> {

    private InputStream inputStream;

    public ProcessReadTask(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public List<String> call() {
        return new BufferedReader(new InputStreamReader(inputStream))
                .lines()
                .collect(Collectors.toList());
    }
}}
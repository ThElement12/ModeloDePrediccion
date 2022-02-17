package main;
import models.Animal;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.estimators.Estimator;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

import java.io.*;

public class Main {
    public static void main(String[] args) throws Exception {

        DataSource dataSource = new DataSource("src/main/java/data/zoo.arff");
        Instances dataSet = dataSource.getDataSet();
        dataSet.setClassIndex(dataSet.numAttributes()-1);

        //Se eliminan los atributos que no vamos a utilizar para nuestro modelo
        Remove remove = new Remove();
        int[] listattributes =new int[]{0};
        remove.setAttributeIndicesArray(listattributes);
        remove.setInputFormat(dataSet);
        Instances datasetFiltrado = Filter.useFilter(dataSet, remove);

        //Se utiliza naiveBayes como clasificador
        NaiveBayes naiveBayes = new NaiveBayes();
        naiveBayes.buildClassifier(datasetFiltrado);
        Evaluation evaluation = new Evaluation(datasetFiltrado);
        evaluation.evaluateModel(naiveBayes, datasetFiltrado);

        //Se declaran los animales a buscar
        Animal[] animals = {new Animal("cat", new String[]{"true", "false", "false", "true", "false", "false", "false", "false", "true", "true", "false", "false", "4", "true", "true", "true", "?"}),
                            new Animal("crab", new String[]{"false", "false", "true", "false", "false", "true", "true", "false", "false", "false", "false", "false", "4", "false", "false", "false", "?"}),
                            new Animal("snake", new String[]{"false", "false", "true", "false", "false", "false", "true", "true", "true", "true", "true", "false", "0", "true", "false", "false", "?"})};

        //Se recorre cada uno para predecir su tipo
        for(int i = 0; i < animals.length; i++){
            predictAnimal(animals[i], datasetFiltrado, naiveBayes);
        }
        //System.out.println("Prueba jevi-----");
        //printconditional(naiveBayes.getConditionalEstimators());

    }
    /**Esta sería nuestra función clave la cual recibiría un animal,
     *  el dataset filtrado y nuestro objeto de bayes. Aquí se carga el dato del animal en un dataset temporal en un
     *  archivo para luego cargar este dataset y clasificarlo con bayes, este dando como resultado la predicción
     *  del tipo del animal.
     */
    static void predictAnimal(Animal animal,Instances datasetFiltrado,  NaiveBayes naiveBayes) throws Exception{
        //Cuando se tiene la nueva instancia se coloca en un archivo arff
        File file = new File("src/main/java/data/zoo-test.arff");
        FileWriter fr = new FileWriter(file, true);
        fr.write(animal.getStringAtt());
        fr.close();

        //Se vuelve a cargar ese dataset con la instancia nueva
        DataSource dataSource2 = new DataSource("src/main/java/data/zoo-test.arff");
        Instances dataset2 = dataSource2.getDataSet();
        //Aquí lo agregamos al dataset principal para hacer la predicción y probar el modelo con esa instancia

        datasetFiltrado.add(dataset2.get(0));
        //System.out.println(datasetFiltrado.toSummaryString());

        Evaluation evaluation = new Evaluation(datasetFiltrado);
        evaluation.evaluateModel(naiveBayes, datasetFiltrado);
        System.out.println(evaluation.toSummaryString("\nResultados\n=====", false));

        int prediction = (int) naiveBayes.classifyInstance(datasetFiltrado.lastInstance());
        resetFile(animal.getStringAtt());
        System.out.println("Resultados De La Prediccion:");
        translateResult(prediction, animal.getName());

    }

    static void printconditional(Estimator[][] estimators){
        for(int i = 0; i < estimators.length; i++){
            for(int j = 0; j < estimators[i].length; j++){
                System.out.print(estimators[i][j]);
            }
            System.out.println("\n");
        }

    }

    static void translateResult(int result, String animalName) {
        String[] result_arr = {"mammal", "bird", "reptile", "fish", "amphibian", "insect", "invertebrate"};
        System.out.println("Se predijo que el animal " + animalName + " seria de tipo: " + result_arr[result]);

    }

    static void resetFile(String last) throws IOException {
        File file = new File("src/main/java/data/zoo-test.arff");
        File temp = new File("src/main/java/data/zoo-test.arff");

        BufferedReader input = new BufferedReader(new FileReader("src/main/java/data/zoo-test.arff"));
        String line = null;
        PrintWriter pw = new PrintWriter(new FileWriter(temp));

        while ((line = input.readLine()) != null) {
            if (!line.trim().equals(last)) {
                pw.println(line);
                pw.flush();
            }
        }
        pw.close();
        input.close();

        if (!file.delete()) {
          //  System.out.println("No se puede borrar el archivo");
        }
        if (!temp.renameTo(file)) {
          //  System.out.println("No se puede renombrar el archivo");
        }

        FileWriter fr = new FileWriter(temp, true);

        fr.write("@relation zoo\n" +
                "\n" +
                "@attribute hair {false, true}\n" +
                "@attribute feathers {false, true}\n" +
                "@attribute eggs {false, true}\n" +
                "@attribute milk {false, true}\n" +
                "@attribute airborne {false, true}\n" +
                "@attribute aquatic {false, true}\n" +
                "@attribute predator {false, true}\n" +
                "@attribute toothed {false, true}\n" +
                "@attribute backbone {false, true}\n" +
                "@attribute breathes {false, true}\n" +
                "@attribute venomous {false, true}\n" +
                "@attribute fins {false, true}\n" +
                "@attribute legs INTEGER [0,9]\n" +
                "@attribute tail {false, true}\n" +
                "@attribute domestic {false, true}\n" +
                "@attribute catsize {false, true}\n" +
                "@attribute type { mammal, bird, reptile, fish, amphibian, insect, invertebrate }\n" +
                "\n" +
                "@data\n");
        fr.close();
    }
}

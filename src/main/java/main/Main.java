package main;
import models.Animal;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

import java.io.*;

public class Main {
    public static void main(String[] args) throws Exception {

        DataSource dataSource = new DataSource("src/main/java/data/zoo.arff");
        Instances dataSet = dataSource.getDataSet();
        dataSet.setClass(dataSet.attribute("type"));
        Remove removeattribute = new Remove();

        int[] listattributes =new int[]{0,17};
        removeattribute.setAttributeIndicesArray(listattributes);
        removeattribute.setInputFormat(dataSet);
        Instances datasetFiltrado = Filter.useFilter(dataSet, removeattribute);

        NaiveBayes naiveBayes = new NaiveBayes();
        naiveBayes.buildClassifier(datasetFiltrado);
        Evaluation evaluation = new Evaluation(datasetFiltrado);
        evaluation.evaluateModel(naiveBayes, datasetFiltrado);

        System.out.println(evaluation.toSummaryString("\nResultados\n=====\n", false));

        Animal dog = new Animal("dog", new String[]{"true", "false", "false", "true", "false", "false", "false", "false", "true", "true", "false", "false", "4", "true", "true", "false"});
        Animal cat = new Animal("cat", new String[]{"true", "false", "false", "true", "false", "false", "false", "false", "true", "true", "false", "false", "4", "true", "true", "true"});
        Animal crab = new Animal("crab", new String[]{"false", "false", "true", "false", "false", "true", "true", "false", "false", "false", "false", "false", "4", "false", "false", "false"});
        Animal pitviper = new Animal("pitvipe", new String[]{"false", "false", "true", "false", "false", "false", "true", "true", "true", "true", "true", "false", "0", "true", "false", "false"});

        File file = new File("src/main/java/data/zoo-test.arff");
        FileWriter fr = new FileWriter(file, true);
        fr.write(crab.getStringAtt());
        fr.close();

        DataSource dataSource2 = new DataSource("src/main/java/data/zoo-test.arff");
        Instances dataset2 = dataSource2.getDataSet();

        datasetFiltrado.add(dataset2.get(0));
        System.out.println(datasetFiltrado.toSummaryString());

        evaluation = new Evaluation(datasetFiltrado);
        evaluation.evaluateModel(naiveBayes, datasetFiltrado);
        System.out.println(evaluation.toSummaryString("\nResultados\n=====\n", false));

        int prediction = (int) naiveBayes.classifyInstance(datasetFiltrado.lastInstance());
        System.out.println(prediction);
        resetFile(crab.getStringAtt());
        System.out.println("\nResultados De La Prediccion: \n");
        translateResult(prediction);
        //System.out.println(naiveBayes.getConditionalEstimators());

    }

    static void translateResult(int result) {
        String[] result_arr = {"mammal", "bird", "reptile", "fish", "amphibian", "insect", "invertebrate"};
        System.out.println("Se predijo que el animal seria de tipo: " + result_arr[result]);
        System.out.println(result);

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
            System.out.println("No se puede borrar el archivo");
        }
        if (!temp.renameTo(file)) {
            System.out.println("No se puede renombrar el archivo");
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
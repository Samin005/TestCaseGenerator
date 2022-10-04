package com.example.testcasegenerator;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Scanner;

public class TestCaseGenerator {
    static String MODEL_PACKAGE_NAME = "model";
    static String REPOSITORY_PACKAGE_NAME = "repository";

    public static void main(String[] args) {
        try {
            File modelFolder = findFile(System.getProperty("user.dir") + "/src", MODEL_PACKAGE_NAME);
            File[] modelsList = modelFolder.listFiles();

            File repositoryFolder = findFile(System.getProperty("user.dir") + "/src", REPOSITORY_PACKAGE_NAME);
            File[] repositoriesList = repositoryFolder.listFiles();

            for (int i = 0; i < modelsList.length; i++) {
                if (modelsList[i].isFile() && modelsList[i].getName().endsWith(".java")) {
                    String modelName = modelsList[i].getName().replace(".java", "");
                    System.out.println("Model name: " + modelName + "\n");
                    String classPath = modelsList[i].getAbsolutePath().replace("\\", "/").replace(":/", ":\\\\").replace(System.getProperty("user.dir").replace("\\", "/").replace(":/", ":\\\\") + "/src/main/java/", "").replace(".java", "").replaceAll("/", ".");
                    Field[] fields = Class.forName(classPath).getConstructor().newInstance().getClass().getDeclaredFields();
                    createFeatureFile(modelName, fields);
                    // find model repository
                    String modelRepositoryName = findModelRepository(modelName, repositoriesList);
                    Method[] methods = Class.forName(classPath).getConstructor().newInstance().getClass().getMethods();
                    System.out.println(Arrays.toString(methods));
                    createStepDefinitionsFile(classPath, modelName, modelRepositoryName, fields, methods);
                    for(Field field: fields) {
                        System.out.println("Field Name: " + field.getName());
                        System.out.println("Field Type: " + field.getType());
                        System.out.println("Field Annotations: " + Arrays.toString(field.getAnnotations()) + "\n");
                    }
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException | NullPointerException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static File findFile(String path, String fName) {
        File f = new File(path);
        if (fName.equalsIgnoreCase(f.getName())) return f;
        if (f.isDirectory()) {
            for (String aChild : f.list()) {
                File ff = findFile(path + File.separator + aChild, fName);
                if (ff != null) return ff;
            }
        }
        return null;
    }

    public static void writeNullableInvalidCases(FileWriter fileWriter, Field[] fields, int notNullableCount) throws IOException {
        int lastNullAppliedIndex = -1;
        for(int i=0; i<notNullableCount; i++) {
            boolean nullAppliedForRow = false;
            for(int j=0; j<fields.length; j++) {
                if(fields[j].getType().toString().endsWith("Integer")) {
                    fileWriter.write("| " + 1 + " ");
                }
                else if(fields[j].getType().toString().endsWith("String")) {
                    if(Arrays.toString(fields[j].getAnnotations()).contains("nullable=false") && !nullAppliedForRow && lastNullAppliedIndex!=j) {
                        fileWriter.write("| null ");
                        nullAppliedForRow = true;
                        lastNullAppliedIndex = j;
                    }
                    else
                        fileWriter.write("| test" + i + " ");
                }
            }
            fileWriter.write("| invalid |\n         ");
        }
    }

    public static void writeUniqueInvalidCases(FileWriter fileWriter, Field[] fields, int uniqueCount) throws IOException {
        int idCounter = 2;
        for(int i=0; i<uniqueCount; i++) {
            for(Field field: fields) {
                if(field.getType().toString().endsWith("Integer")) {
                    fileWriter.write("| " + idCounter + " ");
                }
                else if(field.getType().toString().endsWith("String")) {
                    if(Arrays.toString(field.getAnnotations()).contains("unique=true"))
                        fileWriter.write("| test"+(idCounter-1)+" ");
                    else
                        fileWriter.write("| test"+idCounter+" ");
                }
            }
            fileWriter.write("| invalid |\n         ");
        }
    }

    public static String findModelRepository(String modelName, File[] repositoriesList) throws FileNotFoundException {
        String modelRepositoryName = null;
        Scanner sc;
        for(File repository: repositoriesList) {
            sc = new Scanner(repository);
            while (sc.hasNext()) {
                if(sc.next().contains("Repository<"+modelName)) {
                    modelRepositoryName = repository.getName().replace(".java", "");
                    sc.close();
                    break;
                }
            }
        }
        return modelRepositoryName;
    }

    public static String findModelMethodName(Field field, Method[] methods, String startsWith) {
        String methodName = null;
        for(Method method: methods) {
            if(method.getName().toLowerCase().contains(startsWith+field.getName())) {
                methodName = method.getName();
            }
        }
        return methodName;
    }

    public static void createFeatureFile(String modelName, Field[] fields) throws IOException {
        // create directory if it does not exist
        File directory = new File("src/test/resources");
        directory.mkdir();

        // create empty feature file
        File featureFile = new File(directory, modelName + ".feature");
        featureFile.createNewFile();

        FileWriter fileWriter = new FileWriter(featureFile);
        fileWriter.write("Feature: " + modelName + "\n\n");
        String modelNameLowerCase = modelName.toLowerCase();

        // create scenario
        fileWriter.write("  Scenario Outline: create single "+ modelNameLowerCase+"\n");
        fileWriter.write("      Given delete existing "+ modelNameLowerCase+"s\n");
        fileWriter.write("      And "+ modelNameLowerCase+" properties are ");
        for(Field field: fields) {
            if(field.getType().toString().endsWith("Integer")) {
                fileWriter.write("<" + field.getName() + "> ");
            }
            else if(field.getType().toString().endsWith("String")) {
                fileWriter.write("\"<" + field.getName() + ">\" ");
            }
        }
        fileWriter.write("\n");
        fileWriter.write("      When create current "+ modelNameLowerCase+"\n");
        fileWriter.write("      Then create single "+ modelNameLowerCase+" status should be \"<status>\"\n");
        fileWriter.write("      Examples:\n         ");
        // create example table
        int notNullableCount = 0;
        int uniqueCount = 0;
        for(Field field: fields) {
            fileWriter.write("| " + field.getName() + " ");
            if(!Arrays.toString(field.getAnnotations()).contains("Id()")) {
                if(Arrays.toString(field.getAnnotations()).contains("nullable=false"))
                    notNullableCount++;
                if(Arrays.toString(field.getAnnotations()).contains("unique=true"))
                    uniqueCount++;
            }
        }
        fileWriter.write("| status |\n         ");
        // valid case
        for(Field field: fields) {
            if(field.getType().toString().endsWith("Integer")) {
                fileWriter.write("| " + 1 + " ");
            }
            else if(field.getType().toString().endsWith("String")) {
                fileWriter.write("| test ");
            }
        }
        fileWriter.write("| valid |\n         ");
        // invalid nullable cases
        writeNullableInvalidCases(fileWriter, fields, notNullableCount);
        fileWriter.write("\n");

        // create scenario
        fileWriter.write("  Scenario Outline: create multiple "+ modelNameLowerCase+"s\n");
        fileWriter.write("      Given "+ modelNameLowerCase+" properties are ");
        for(Field field: fields) {
            if(field.getType().toString().endsWith("Integer")) {
                fileWriter.write("<" + field.getName() + "> ");
            }
            else if(field.getType().toString().endsWith("String")) {
                fileWriter.write("\"<" + field.getName() + ">\" ");
            }
        }
        fileWriter.write("\n");
        fileWriter.write("      When create current "+ modelNameLowerCase+"\n");
        fileWriter.write("      Then create "+ modelNameLowerCase+" status should be \"<status>\"\n");
        fileWriter.write("      Examples:\n         ");
        // create example table
        for(Field field: fields)
            fileWriter.write("| " + field.getName() + " ");
        fileWriter.write("| status |\n         ");
        // valid case
        int idCounter = 1;
        for(int i=0; i<2; i++){
            for(Field field: fields) {
                if(field.getType().toString().endsWith("Integer")) {
                    fileWriter.write("| " + idCounter + " ");
                }
                else if(field.getType().toString().endsWith("String")) {
                    fileWriter.write("| test"+idCounter+" ");
                }
            }
            fileWriter.write("| valid |\n         ");
            idCounter++;
        }
        // invalid unique cases
        writeUniqueInvalidCases(fileWriter, fields, uniqueCount);
        // invalid nullable cases
        writeNullableInvalidCases(fileWriter, fields, notNullableCount);
        fileWriter.write("\n");

        fileWriter.flush();
        fileWriter.close();
    }

    public static void createStepDefinitionsFile(String classPath, String modelName, String modelRepositoryName, Field[] fields, Method[] methods) throws IOException {
        String commonClassPath  = classPath.replace("."+MODEL_PACKAGE_NAME+"."+modelName, "");
        String packageName = "cucumber";

        // create directory if it does not exist
        File directory = new File("src/test/java/"+commonClassPath.replaceAll("\\.", "/")+"/"+packageName);
        directory.mkdir();

        // create empty feature file
        File featureFile = new File(directory, modelName + "StepDefinitions.java");
        featureFile.createNewFile();

        FileWriter fileWriter = new FileWriter(featureFile);
        // adding required imports
        fileWriter.write("package " + commonClassPath + "." + packageName + ";\n\n");
        fileWriter.write("import io.cucumber.java.en.*;\n");
        fileWriter.write("import org.mockito.internal.matchers.apachecommons.ReflectionEquals;\n");
        fileWriter.write("import org.springframework.beans.factory.annotation.Autowired;\n");
        fileWriter.write("import static org.junit.Assert.*;\n");
        fileWriter.write("import "+classPath+";\n");
        if(modelRepositoryName != null) {
            fileWriter.write("import "+commonClassPath+"."+REPOSITORY_PACKAGE_NAME+"."+modelRepositoryName+";\n\n");
            // creating class
            fileWriter.write("public class " + modelName + "StepDefinitions {\n");
            // adding class fields
            fileWriter.write("  @Autowired\n");
            String repositoryObjectName = modelRepositoryName.replace(modelRepositoryName.charAt(0) + "", (modelRepositoryName.charAt(0) + "").toLowerCase());
            fileWriter.write("  private "+modelRepositoryName+" "+repositoryObjectName+";\n");
            fileWriter.write("  private "+modelName+" current"+modelName+" = new "+modelName+"();\n\n");
            String modelNameLowerCase = modelName.toLowerCase();
            // creating method
            fileWriter.write("  @Given(\"delete existing "+modelNameLowerCase+"s\")\n");
            fileWriter.write("  public void deleteExisting"+modelName+"s() {\n");
            fileWriter.write("      "+repositoryObjectName+".deleteAll();\n");
            fileWriter.write("      assertEquals(0, "+repositoryObjectName+".count());\n");
            fileWriter.write("      System.out.println(\"Deleted all "+modelNameLowerCase+"s\");\n");
            fileWriter.write("  }\n\n");
            // creating method
            fileWriter.write("  @Given(\""+modelNameLowerCase+" properties are");
            for(Field field: fields) {
                if(field.getType().toString().endsWith("Integer")) {
                    fileWriter.write(" {int}");
                }
                else if(field.getType().toString().endsWith("String")) {
                    fileWriter.write(" {string}");
                }
            }
            fileWriter.write("\")\n");
            fileWriter.write("  public void set"+modelName+"Properties(");
            for(int i=0; i<fields.length; i++) {
                if(fields[i].getType().toString().endsWith("Integer")) {
                    fileWriter.write("int " + fields[i].getName());
                }
                else if(fields[i].getType().toString().endsWith("String")) {
                    fileWriter.write("String " + fields[i].getName());
                }
                if(i!=fields.length-1)
                    fileWriter.write(", ");
            }
            fileWriter.write(") {\n");
            for(Field field: fields) {
                for(Method method: methods) {
                    if(method.getName().toLowerCase().contains("set"+field.getName())) {
                        if(field.getType().toString().endsWith("String"))
                            fileWriter.write("      "+field.getName()+" = "+field.getName()+".equals(\"null\") ? null : "+field.getName()+";\n");
                        fileWriter.write("      current"+modelName+"."+method.getName()+"("+field.getName()+");\n");
                    }
                }
            }
            fileWriter.write("  }\n\n");
            // creating method
            fileWriter.write("  @When(\"create current "+modelNameLowerCase+"\")\n");
            fileWriter.write("  public void createCurrent"+modelName+"() {\n");
            fileWriter.write("      try{\n");
            fileWriter.write("          "+repositoryObjectName+".save(current"+modelName+");\n");
            for(Field field: fields) {
                if(Arrays.toString(field.getAnnotations()).contains("Id()")) {
                    fileWriter.write("          System.out.println(\"Saved "+modelNameLowerCase+" with id: \" + current"+modelName+"."+findModelMethodName(field, methods, "get")+"());\n");
                }
            }
            fileWriter.write("      } catch (Exception e) {\n");
            fileWriter.write("          e.printStackTrace();\n");
            fileWriter.write("      }\n");
            fileWriter.write("  }\n\n");
            // creating method
            fileWriter.write("  private "+modelName+" get"+modelName+"ById(int id) {\n");
            fileWriter.write("      if("+repositoryObjectName+".findById(id).isPresent())\n");
            fileWriter.write("          return "+repositoryObjectName+".findById(id).get();\n");
            fileWriter.write("      else return null;\n");
            fileWriter.write("  }\n\n");
            // creating method
            fileWriter.write("  @Then(\"create single user status should be {string}\")\n");
            fileWriter.write("  public void checkSingle"+modelName+"CreateStatus(String status) {\n");
            for(Field field: fields) {
                if (Arrays.toString(field.getAnnotations()).contains("Id()")) {
                    fileWriter.write("      "+modelName+" created"+modelName+" = get"+modelName+"ById"+"(current"+modelName+"."+findModelMethodName(field, methods, "get")+"());\n");
                }
            }
            fileWriter.write("      if(status.equals(\"valid\")) {\n");
            fileWriter.write("          assertEquals(1, "+repositoryObjectName+".count());\n");
            fileWriter.write("          assertTrue(new ReflectionEquals(created"+modelName+").matches(current"+modelName+"));\n");
            fileWriter.write("      }\n");
            fileWriter.write("      else {\n");
            fileWriter.write("          assertNotEquals(1, "+repositoryObjectName+".count());\n");
            fileWriter.write("          assertFalse(new ReflectionEquals(created"+modelName+").matches(current"+modelName+"));\n");
            fileWriter.write("      }\n");
            fileWriter.write("  }\n\n");
            // creating method
            fileWriter.write("  @Then(\"create user status should be {string}\")\n");
            fileWriter.write("  public void check"+modelName+"CreateStatus(String status) {\n");
            for(Field field: fields) {
                if (Arrays.toString(field.getAnnotations()).contains("Id()")) {
                    fileWriter.write("      "+modelName+" created"+modelName+" = get"+modelName+"ById"+"(current"+modelName+"."+findModelMethodName(field, methods, "get")+"());\n");
                }
            }
            fileWriter.write("      if(status.equals(\"valid\")) {\n");
            fileWriter.write("          assertTrue(new ReflectionEquals(created"+modelName+").matches(current"+modelName+"));\n");
            fileWriter.write("      }\n");
            fileWriter.write("      else {\n");
            fileWriter.write("          assertFalse(new ReflectionEquals(created"+modelName+").matches(current"+modelName+"));\n");
            fileWriter.write("      }\n");
            fileWriter.write("  }\n\n");
            fileWriter.write("}");
        }
        else
            fileWriter.write("// Could not locate repository for model: " + modelName);

        fileWriter.flush();
        fileWriter.close();
    }
}

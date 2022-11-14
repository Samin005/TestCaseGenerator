package com.example.testcasegenerator;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
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

            for (File modelFile : modelsList) {
                if(modelFile.isFile() && modelFile.getName().endsWith(".java")) {
                    String modelName = modelFile.getName().replace(".java", "");
                    System.out.println("Model name: " + modelName + "\n");
                    String classPath = getClasspathFromModelFile(modelFile);
                    Field[] fields = getFieldsFromModelClassPath(classPath);
                    for (Field field : fields) {
                        System.out.println("Field Name: " + field.getName());
                        System.out.println("Field Type: " + field.getType());
                        System.out.println("Field Annotations: " + Arrays.toString(field.getAnnotations()) + "\n");
                    }
                    ArrayList<Field> relationalFields = getRelationalFields(fields);
                    createFeatureFile(modelName, fields, relationalFields);
                    // find model repository
                    String modelRepositoryName = findModelRepository(modelName, repositoriesList);
                    Method[] methods = Class.forName(classPath).getConstructor().newInstance().getClass().getMethods();
                    createStepDefinitionsFile(classPath, modelName, modelRepositoryName, fields, relationalFields, methods, modelsList, repositoriesList);
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException | NullPointerException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static File findFile(String path, String fName) {
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

    private static String getClasspathFromModelFile(File modelFile) {
        return modelFile.getAbsolutePath().replace("\\", "/").replace(":/", ":\\\\").replace(System.getProperty("user.dir").replace("\\", "/").replace(":/", ":\\\\") + "/src/main/java/", "").replace(".java", "").replaceAll("/", ".");
    }

    private static Field[] getFieldsFromModelClassPath(String modelClassPath) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return Class.forName(modelClassPath).getConstructor().newInstance().getClass().getDeclaredFields();
    }

    private static String getModelNameFromType(String type) {
        return type.replaceAll("class \\S+"+MODEL_PACKAGE_NAME+".", "");
    }

    private static ArrayList<Field> getRelationalFields(Field[] fields) {
        ArrayList<Field> relationalFields = new ArrayList<>();
        for (Field field : fields) {
            if(isRelationalField(field) && Arrays.toString(field.getAnnotations()).contains("optional=false"))
                relationalFields.add(field);
        }
        return relationalFields;
    }

    private static boolean isRelationalField(Field field) {
        return Arrays.toString(field.getAnnotations()).contains("OneToOne") | Arrays.toString(field.getAnnotations()).contains("ManyToOne") | Arrays.toString(field.getAnnotations()).contains("ManyToMany");
    }

    private static boolean isCustomModelField(Field field) {
        return field.getType().toString().contains(MODEL_PACKAGE_NAME + ".");
    }

    private static void writeNullableInvalidCases(FileWriter fileWriter, Field[] fields, int notNullableCount) throws IOException {
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
                else if(isCustomModelField(fields[j])) {
                    if(Arrays.toString(fields[j].getAnnotations()).contains("optional=false") && !nullAppliedForRow && lastNullAppliedIndex!=j) {
                        fileWriter.write("| -1 ");
                        nullAppliedForRow = true;
                        lastNullAppliedIndex = j;
                    }
                    else
                        fileWriter.write("| 1 ");
                }
            }
            fileWriter.write("| invalid |\n         ");
        }
    }

    private static void writeUniqueInvalidCases(FileWriter fileWriter, Field[] fields, int uniqueCount) throws IOException {
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

    private static String findModelRepository(String modelName, File[] repositoriesList) throws FileNotFoundException {
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

    private static String findModelMethodName(Field field, Method[] methods, String startsWith) {
        String methodName = null;
        for(Method method: methods) {
            if(method.getName().toLowerCase().contains(startsWith+field.getName())) {
                methodName = method.getName();
            }
        }
        return methodName;
    }

    private static String findIdFieldName(Field[] fields) {
        for(Field field: fields) {
            if(Arrays.toString(field.getAnnotations()).contains("Id()")) {
                return field.getName();
            }
        }
        return null;
    }

    private static void writeBackgroundForRelationalFields(FileWriter fileWriter, ArrayList<Field> relationalFields) throws IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if(relationalFields.size() > 0) {
            fileWriter.write("  Background: \n");
            for(int i=0; i<relationalFields.size(); i++) {
                String modelNameLowerCase = getModelNameFromType(relationalFields.get(i).getType().toString()).toLowerCase();
                Field[] modelFields = getFieldsFromModelClassPath(relationalFields.get(i).getType().toString().replace("class ", ""));
                String command = "Given";
                if(i>0)
                    command = "And";
                writeStatementsForCreatingCurrentModelWithAllFields(fileWriter, modelFields, modelNameLowerCase, command, "values");
            }
            fileWriter.write("\n");
        }
    }

    private static void writeStatementsForCreatingCurrentModelWithAllFields(FileWriter fileWriter, Field[] fields, String modelNameLowerCase, String command, String format) throws IOException {
        fileWriter.write("      "+command+" create "+ modelNameLowerCase+" with values ");
        for(Field field: fields) {
            if(field.getType().toString().endsWith("Integer")) {
                if(format.equals("example table"))
                    fileWriter.write("<" + field.getName() + "> ");
                else fileWriter.write("1 ");
            }
            else if(field.getType().toString().endsWith("String")) {
                if(format.equals("example table"))
                    fileWriter.write("\"<" + field.getName() + ">\" ");
                else fileWriter.write("\"test-" + field.getName() + "\" ");
            }
            else if(isCustomModelField(field)){
                if(format.equals("example table"))
                    fileWriter.write("<" + field.getName() + "_id> ");
                else fileWriter.write("1 ");
            }
        }
        fileWriter.write("\n");
    }

    private static void writeValidExampleRowsWithAllFields(int count, FileWriter fileWriter, Field[] fields) throws IOException {
        for(int i=1; i<=count; i++){
            for(Field field: fields) {
                if(field.getType().toString().endsWith("Integer")) {
                    fileWriter.write("| " + i + " ");
                }
                else if(field.getType().toString().endsWith("String")) {
                    fileWriter.write("| test"+i+" ");
                }
                else if(isCustomModelField(field))
                    fileWriter.write("| " + 1 + " ");
            }
            fileWriter.write("| valid |\n         ");
        }
    }

    private static void writeExampleTableHeaderForCreation(FileWriter fileWriter, Field[] fields) throws IOException {
        for(Field field: fields) {
            if(field.getType().toString().endsWith("Integer") | field.getType().toString().endsWith("String")) {
                fileWriter.write("| " + field.getName() + " ");
            }
            else if(isCustomModelField(field))
                fileWriter.write("| " + field.getName() + "_id ");
        }
        fileWriter.write("| status |\n         ");
    }

    private static void writeStatementForDatabaseSnapshot(FileWriter fileWriter, String modelNameLowerCase, String command) throws IOException {
        fileWriter.write("      " + command + " save database snapshot for "+ modelNameLowerCase+"s and rest of the world\n");
    }

    private static void writeTestScenarioCreateSingleModel(FileWriter fileWriter, Field[] fields, String modelNameLowerCase, int notNullableCount) throws IOException {
        fileWriter.write("  Scenario Outline: create single "+ modelNameLowerCase+"\n");
        fileWriter.write("      Given delete existing "+ modelNameLowerCase+"s\n");
        writeStatementForDatabaseSnapshot(fileWriter, modelNameLowerCase, "And");
        writeStatementsForCreatingCurrentModelWithAllFields(fileWriter, fields, modelNameLowerCase, "When", "example table");
        fileWriter.write("      Then create single "+ modelNameLowerCase+" status should be \"<status>\" with snapshot validation\n");
        fileWriter.write("      Examples:\n         ");
        // create example table
        writeExampleTableHeaderForCreation(fileWriter, fields);

        // valid case
        for(Field field: fields) {
            if(field.getType().toString().endsWith("Integer")) {
                fileWriter.write("| " + 1 + " ");
            }
            else if(field.getType().toString().endsWith("String")) {
                fileWriter.write("| test ");
            }
            else if(isCustomModelField(field))
                fileWriter.write("| " + 1 + " ");
        }
        fileWriter.write("| valid |\n         ");
        // invalid nullable cases
        writeNullableInvalidCases(fileWriter, fields, notNullableCount);
        fileWriter.write("\n");
    }

    private static void writeTestScenarioCreateMultipleModel(FileWriter fileWriter, Field[] fields, String modelNameLowerCase, int notNullableCount, int uniqueCount) throws IOException {
        fileWriter.write("  Scenario Outline: create multiple "+ modelNameLowerCase+"s\n");
        writeStatementForDatabaseSnapshot(fileWriter, modelNameLowerCase, "Given");
        writeStatementsForCreatingCurrentModelWithAllFields(fileWriter, fields, modelNameLowerCase, "Given", "example table");
        fileWriter.write("      Then create "+ modelNameLowerCase+" status should be \"<status>\" with snapshot validation\n");
        fileWriter.write("      Examples:\n         ");
        // create example table
        writeExampleTableHeaderForCreation(fileWriter, fields);

        // valid case
        writeValidExampleRowsWithAllFields(2, fileWriter, fields);

        // invalid unique cases
        writeUniqueInvalidCases(fileWriter, fields, uniqueCount);
        // invalid nullable cases
        writeNullableInvalidCases(fileWriter, fields, notNullableCount);
        fileWriter.write("\n");
    }

    private static void writeTestScenarioFetchOrDeleteBeforeModelCreation(String command, String commanding, String idFieldName, FileWriter fileWriter, String modelNameLowerCase) throws IOException {
        fileWriter.write("  Scenario Outline: "+command+" "+modelNameLowerCase+" without creation\n");
        fileWriter.write("      Given delete existing "+ modelNameLowerCase+"s\n");
        writeStatementForDatabaseSnapshot(fileWriter, modelNameLowerCase, "And");
        fileWriter.write("      Then "+commanding+" "+modelNameLowerCase+" <"+idFieldName+"> should be \"<status>\" with snapshot validation\n");
        fileWriter.write("      Examples:\n         ");
        // create example table
        fileWriter.write("| " + idFieldName + " ");
        fileWriter.write("| status |\n         ");
        // invalid cases
        for(int i=1; i<3; i++){
            fileWriter.write("| " + i + " ");
            fileWriter.write("| invalid |\n         ");
        }
        fileWriter.write("\n");
    }

    private static void writeTestScenarioFetchOrDeleteAfterModelCreation(String command, String commanding, String idFieldName, FileWriter fileWriter, Field[] fields, String modelNameLowerCase) throws IOException {
        fileWriter.write("  Scenario Outline: "+command+" "+modelNameLowerCase+" after creation\n");
        fileWriter.write("      Given delete existing "+ modelNameLowerCase+"s\n");
        writeStatementsForCreatingCurrentModelWithAllFields(fileWriter, fields, modelNameLowerCase, "And", "example table");
        writeStatementForDatabaseSnapshot(fileWriter, modelNameLowerCase, "And");
        fileWriter.write("      Then "+commanding+" "+modelNameLowerCase+" <"+idFieldName+"> should be \"<status>\" with snapshot validation\n");
        fileWriter.write("      Examples:\n         ");
        // create example table
        writeExampleTableHeaderForCreation(fileWriter, fields);
        // valid cases
        writeValidExampleRowsWithAllFields(2, fileWriter, fields);
        fileWriter.write("\n");
    }

    public static void createFeatureFile(String modelName, Field[] fields, ArrayList<Field> relationalFields) throws IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        // create directory if it does not exist
        File directory = new File("src/test/resources");
        directory.mkdir();

        // create empty feature file
        File featureFile = new File(directory, modelName + ".feature");
        featureFile.createNewFile();

        FileWriter fileWriter = new FileWriter(featureFile);
        fileWriter.write("Feature: " + modelName + "\n\n");
        String modelNameLowerCase = modelName.toLowerCase();

        String idFieldName = findIdFieldName(fields);
        int notNullableCount = 0;
        int uniqueCount = 0;
        for(Field field: fields) {
            if(!Arrays.toString(field.getAnnotations()).contains("Id()")) {
                if(Arrays.toString(field.getAnnotations()).contains("nullable=false") || (isRelationalField(field) && Arrays.toString(field.getAnnotations()).contains("optional=false")))
                    notNullableCount++;
                if(Arrays.toString(field.getAnnotations()).contains("unique=true"))
                    uniqueCount++;
            }
        }

        // create background
        writeBackgroundForRelationalFields(fileWriter, relationalFields);

        // create scenario
        writeTestScenarioCreateSingleModel(fileWriter, fields, modelNameLowerCase, notNullableCount);

        // create scenario
        writeTestScenarioCreateMultipleModel(fileWriter, fields, modelNameLowerCase, notNullableCount, uniqueCount);

        // create scenario
        writeTestScenarioFetchOrDeleteBeforeModelCreation("fetch", "fetching", idFieldName, fileWriter, modelNameLowerCase);

        // create scenario
        writeTestScenarioFetchOrDeleteAfterModelCreation("fetch", "fetching", idFieldName, fileWriter, fields, modelNameLowerCase);

        // create scenario
        writeTestScenarioFetchOrDeleteBeforeModelCreation("delete", "deleting", idFieldName, fileWriter, modelNameLowerCase);

        // create scenario
        writeTestScenarioFetchOrDeleteAfterModelCreation("delete", "deleting", idFieldName, fileWriter, fields, modelNameLowerCase);

        fileWriter.flush();
        fileWriter.close();
    }

    private static String getClassObjectName(String className) {
        return className.replace(className.charAt(0) + "", (className.charAt(0) + "").toLowerCase());
    }

    private static void writeStepDefinitionMethodDeleteExisting(FileWriter fileWriter, String modelName, String modelNameLowerCase, String repositoryObjectName) throws IOException {
        fileWriter.write("  @Given(\"delete existing "+modelNameLowerCase+"s\")\n");
        fileWriter.write("  public void deleteExisting"+modelName+"s() {\n");
        fileWriter.write("      "+repositoryObjectName+".deleteAll();\n");
        fileWriter.write("      assertEquals(0, "+repositoryObjectName+".count());\n");
        fileWriter.write("      System.out.println(\"Deleted all "+modelNameLowerCase+"s\");\n");
        fileWriter.write("  }\n\n");
    }

    private static void writeStepDefinitionMethodCreateModelWithValues(FileWriter fileWriter, Field[] fields, Method[] methods, String repositoryObjectName, String modelName, String modelNameLowerCase, File[] repositoriesList) throws IOException {
        fileWriter.write("  @Given(\"create "+modelNameLowerCase+" with values");
        for(Field field: fields) {
            if(field.getType().toString().endsWith("Integer") || isCustomModelField(field)) {
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
            else if(isCustomModelField(fields[i]))
                fileWriter.write("int " + fields[i].getName()+"_id");
            if(i!=fields.length-1)
                fileWriter.write(", ");
        }
        fileWriter.write(") {\n");
        for(Field field: fields) {
            for(Method method: methods) {
                if(method.getName().toLowerCase().contains("set"+field.getName())) {
                    if(field.getType().toString().endsWith("String"))
                        fileWriter.write("      "+field.getName()+" = "+field.getName()+".equals(\"null\") ? null : "+field.getName()+";\n");
                    if(isCustomModelField(field)){
                        fileWriter.write("      if(" + field.getName()+"_id != -1) \n");
                        fileWriter.write("          current"+modelName+"."+method.getName()+"("+getClassObjectName(findModelRepository(getModelNameFromType(field.getType().toString()), repositoriesList))+".findById("+field.getName()+"_id).get());\n");
                    }
                    else
                        fileWriter.write("      current"+modelName+"."+method.getName()+"("+field.getName()+");\n");
                }
            }
        }
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
    }

    private static void writeMethodGetModelById(FileWriter fileWriter, String modelName, String repositoryObjectName) throws IOException{
        fileWriter.write("  private "+modelName+" get"+modelName+"ById(int id) {\n");
        fileWriter.write("      if("+repositoryObjectName+".findById(id).isPresent())\n");
        fileWriter.write("          return "+repositoryObjectName+".findById(id).get();\n");
        fileWriter.write("      else return null;\n");
        fileWriter.write("  }\n\n");
    }

    private static void writeStepDefinitionMethodSaveDatabaseSnapshot(FileWriter fileWriter, String modelNameLowerCase, File[] modelsList, File[] repositoriesList) throws IOException {
        fileWriter.write("  @Given(\"save database snapshot for "+modelNameLowerCase+"s and rest of the world\")\n");
        fileWriter.write("  public void saveDatabaseSnapshot() {\n");
        for(File modelFile: modelsList) {
            if(modelFile.isFile() && modelFile.getName().endsWith(".java")) {
                String tempModelName = modelFile.getName().replace(".java", "");
                String repositoryName = findModelRepository(tempModelName, repositoriesList);
                fileWriter.write("      "+getClassObjectName(tempModelName)+"Snapshot = "+getClassObjectName(repositoryName)+".findAll();\n");
            }

        }
        fileWriter.write("  }\n\n");
    }

    private static void writeMethodAssertEntityEquals(FileWriter fileWriter) throws IOException {
        fileWriter.write("  private void assertEntityEquals(Iterable expectedEntityIterable, Iterable actualEntityIterable, String[] foreignKeys) {\n");
        fileWriter.write("      Object[] expectedEntity = new ArrayList((Collection) expectedEntityIterable).toArray();\n");
        fileWriter.write("      Object[] actualEntity = new ArrayList((Collection) actualEntityIterable).toArray();\n");
        fileWriter.write("      assertEquals(expectedEntity.length, actualEntity.length);\n");
        fileWriter.write("      for(int i = 0; i < expectedEntity.length; i++)\n");
        fileWriter.write("          assertTrue(new ReflectionEquals(expectedEntity[i], foreignKeys).matches(actualEntity[i]));\n");
        fileWriter.write("  }\n\n");
    }

    private static void writeMethodAssertRemainingEntries(FileWriter fileWriter) throws IOException {
        fileWriter.write("  private void assertRemainingEntries(Iterable expectedEntriesIterable, Iterable actualEntriesIterable, String[] foreignKeys) {\n");
        fileWriter.write("      ArrayList actualEntriesList = new ArrayList((Collection) actualEntriesIterable);\n");
        fileWriter.write("      actualEntriesList.remove(actualEntriesList.size() - 1);\n");
        fileWriter.write("      Object[] actualEntriesWithoutLatest = actualEntriesList.toArray();\n");
        fileWriter.write("      Object[] expectedEntries = new ArrayList((Collection) expectedEntriesIterable).toArray();\n");
        fileWriter.write("      assertEquals(expectedEntries.length, actualEntriesWithoutLatest.length);\n");
        fileWriter.write("      for(int i = 0; i < expectedEntries.length; i++)\n");
        fileWriter.write("          assertTrue(new ReflectionEquals(expectedEntries[i], foreignKeys).matches(actualEntriesWithoutLatest[i]));\n");
        fileWriter.write("  }\n\n");
    }

    private static void writeMethodAssertModelObjectEquals(FileWriter fileWriter, String modelName, String modelNameLowerCase, ArrayList<Field> relationalFields) throws IOException {
        fileWriter.write("  private void assert"+modelName+"ObjectEquals("+modelName+" "+modelNameLowerCase+") {\n");
        fileWriter.write("      assertTrue(new ReflectionEquals("+modelNameLowerCase);
        if(!relationalFields.isEmpty()) {
            fileWriter.write(", new String[]{");
            for(int i=0; i<relationalFields.size(); i++) {
                if(i==0)
                    fileWriter.write("\""+relationalFields.get(i).getName()+"\"");
                else
                    fileWriter.write(", \""+relationalFields.get(i).getName()+"\"");
            }
            fileWriter.write("}");
        }
        fileWriter.write(").matches(current"+modelName+"));\n");
        if(!relationalFields.isEmpty()) {
            for(Field relationalField: relationalFields) {
                fileWriter.write("      assertTrue(new ReflectionEquals("+modelNameLowerCase+".get"+getModelNameFromType(relationalField.getType().toString())+"().getId()).matches(current"+modelName+".get"+getModelNameFromType(relationalField.getType().toString())+"().getId()));\n");
            }
        }
        fileWriter.write("  }\n\n");

    }

    private static String getForeignKeysArrayString(ArrayList<Field> relationalFields) {
        String foreignKeysArrayString = ", null";
        if(!relationalFields.isEmpty()) {
            foreignKeysArrayString = ", new String[]{";
            for(int i=0; i<relationalFields.size(); i++) {
                if(i==0)
                    foreignKeysArrayString += "\""+relationalFields.get(i).getName()+"\"";
                else
                    foreignKeysArrayString += ", \""+relationalFields.get(i).getName()+"\"";
            }
            foreignKeysArrayString += "}";
        }
        return foreignKeysArrayString;
    }

    private static void writeMethodAssertOtherEntitiesUnchanged(FileWriter fileWriter, File[] modelsList, File[] repositoriesList, String currentModelName) throws IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        fileWriter.write("  private void assertOtherEntitiesUnchanged() {\n");
        for(File modelFile: modelsList) {
            if(modelFile.isFile() && modelFile.getName().endsWith(".java")) {
                String modelName = modelFile.getName().replace(".java", "");
                if(!modelName.equals(currentModelName)) {
                    String modelRepositoryName = findModelRepository(modelName, repositoriesList);
                    ArrayList<Field> modelRelationalFields = getRelationalFields(getFieldsFromModelClassPath(getClasspathFromModelFile(modelFile)));
                    fileWriter.write("      assertEntityEquals("+getClassObjectName(modelName)+"Snapshot, "+getClassObjectName(modelRepositoryName)+".findAll()"+getForeignKeysArrayString(modelRelationalFields)+");\n");
                }
            }

        }
        fileWriter.write("  }\n\n");
    }

    private static void writeMethodAssertCreationStatusWithSnapshotValidation(FileWriter fileWriter, String modelName, String modelNameLowerCase, ArrayList<Field> relationalFields) throws IOException {
        fileWriter.write("  private void assertCreationStatusWithSnapshotValidation("+modelName+" created"+modelName+") {\n");
        fileWriter.write("      assert"+modelName+"ObjectEquals(created"+modelName+");\n");
        fileWriter.write("      assertRemainingEntries("+modelNameLowerCase+"Snapshot, "+modelNameLowerCase+"Repository.findAll()"+getForeignKeysArrayString(relationalFields)+");\n\n");
        fileWriter.write("      assertOtherEntitiesUnchanged();\n");
        fileWriter.write("  }\n\n");
    }

    private static void writeMethodAssertSnapshotUnchanged(FileWriter fileWriter, String modelNameLowerCase, ArrayList<Field> relationalFields) throws IOException {
        fileWriter.write("  private void assertSnapshotUnchanged() {\n");
        fileWriter.write("      assertEntityEquals("+modelNameLowerCase+"Snapshot, "+modelNameLowerCase+"Repository.findAll()"+getForeignKeysArrayString(relationalFields)+");\n");
        fileWriter.write("      assertOtherEntitiesUnchanged();\n");
        fileWriter.write("  }\n\n");
    }

    private static void writeStepDefinitionMethodCheckSingleModelCreation(FileWriter fileWriter, Field[] fields, Method[] methods, String modelName, String modelNameLowerCase, String repositoryObjectName) throws IOException {
        fileWriter.write("  @Then(\"create single "+modelNameLowerCase+" status should be {string} with snapshot validation\")\n");
        fileWriter.write("  public void checkSingle"+modelName+"CreateStatus(String status) {\n");
        for(Field field: fields) {
            if (Arrays.toString(field.getAnnotations()).contains("Id()")) {
                fileWriter.write("      "+modelName+" created"+modelName+" = get"+modelName+"ById"+"(current"+modelName+"."+findModelMethodName(field, methods, "get")+"());\n");
            }
        }
        fileWriter.write("      if(status.equals(\"valid\")) {\n");
        fileWriter.write("          assertEquals(1, "+repositoryObjectName+".count());\n");
        fileWriter.write("          assertCreationStatusWithSnapshotValidation(created"+modelName+");\n");
        fileWriter.write("      }\n");
        fileWriter.write("      else {\n");
        fileWriter.write("          assertNotEquals(1, "+repositoryObjectName+".count());\n");
        fileWriter.write("          assertSnapshotUnchanged();\n");
        fileWriter.write("          assertNull(created"+modelName+");\n");
        fileWriter.write("      }\n");
        fileWriter.write("  }\n\n");
    }

    private static void writeStepDefinitionMethodCheckModelCreation(FileWriter fileWriter, Field[] fields, Method[] methods, String modelName, String modelNameLowerCase) throws IOException {
        fileWriter.write("  @Then(\"create "+modelNameLowerCase+" status should be {string} with snapshot validation\")\n");
        fileWriter.write("  public void check"+modelName+"CreateStatus(String status) {\n");
        for(Field field: fields) {
            if (Arrays.toString(field.getAnnotations()).contains("Id()")) {
                fileWriter.write("      "+modelName+" created"+modelName+" = get"+modelName+"ById"+"(current"+modelName+"."+findModelMethodName(field, methods, "get")+"());\n");
            }
        }
        fileWriter.write("      if(status.equals(\"valid\")) {\n");
        fileWriter.write("          assertCreationStatusWithSnapshotValidation(created"+modelName+");\n");
        fileWriter.write("      }\n");
        fileWriter.write("      else assertSnapshotUnchanged();\n");
        fileWriter.write("  }\n\n");
    }

    private static void writeStepDefinitionMethodCheckFetchingModel(FileWriter fileWriter, String modelName, String modelNameLowerCase) throws IOException {
        fileWriter.write("  @Then(\"fetching "+modelNameLowerCase+" {int} should be {string} with snapshot validation\")\n");
        fileWriter.write("  public void fetching"+modelName+"Status(int id, String status) {\n");
        fileWriter.write("      "+modelName+" fetched"+modelName+" = get"+modelName+"ById(id);\n");
        fileWriter.write("      if(status.equals(\"valid\")) {\n");
        fileWriter.write("          assert"+modelName+"ObjectEquals(fetched"+modelName+");\n");
        fileWriter.write("      }\n");
        fileWriter.write("      else assertNull(fetched"+modelName+");\n");
        fileWriter.write("      assertSnapshotUnchanged();\n");
        fileWriter.write("  }\n\n");
    }

    private static void writeMethodAssertDeletionStatusWithSnapshotValidation(FileWriter fileWriter, String modelNameLowerCase, ArrayList<Field> relationalFields) throws IOException {
        fileWriter.write("  private void assertDeletionStatusWithSnapshotValidation() {\n");
        fileWriter.write("      assertRemainingEntries("+modelNameLowerCase+"Repository.findAll(), "+modelNameLowerCase+"Snapshot"+getForeignKeysArrayString(relationalFields)+");\n\n");
        fileWriter.write("      assertOtherEntitiesUnchanged();\n");
        fileWriter.write("  }\n\n");
    }

    private static void writeStepDefinitionMethodCheckDeletingModel(FileWriter fileWriter, String modelName, String modelNameLowerCase, String repositoryObjectName) throws IOException {
        fileWriter.write("  @Then(\"deleting "+modelNameLowerCase+" {int} should be {string} with snapshot validation\")\n");
        fileWriter.write("  public void deleting"+modelName+"Status(int id, String status) {\n");
        fileWriter.write("      try{\n");
        fileWriter.write("          "+repositoryObjectName+".deleteById(id);\n");
        fileWriter.write("          assertEquals(\"valid\", status);\n");
        fileWriter.write("          assertDeletionStatusWithSnapshotValidation();\n");
        fileWriter.write("      } catch (Exception e) {\n");
        fileWriter.write("          e.printStackTrace();\n");
        fileWriter.write("          assertEquals(\"invalid\", status);\n");
        fileWriter.write("          assertSnapshotUnchanged();\n");
        fileWriter.write("      }\n");
        fileWriter.write("  }\n\n");
    }

    public static void createStepDefinitionsFile(String classPath, String modelName, String modelRepositoryName, Field[] fields, ArrayList<Field> relationalFields, Method[] methods, File[] modelsList, File[] repositoriesList) throws IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
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
        fileWriter.write("import java.util.*;\n");
        fileWriter.write("import "+commonClassPath+"."+MODEL_PACKAGE_NAME+".*;\n");
        if(modelRepositoryName != null) {
            fileWriter.write("import "+commonClassPath+"."+REPOSITORY_PACKAGE_NAME+".*;\n\n");
            // creating class
            fileWriter.write("public class " + modelName + "StepDefinitions {\n");
            // adding class fields
            String repositoryObjectName = getClassObjectName(modelRepositoryName);
            for(File modelFile: modelsList) {
                if(modelFile.isFile() && modelFile.getName().endsWith(".java")) {
                    String tempModelName = modelFile.getName().replace(".java", "");
                    String repositoryName = findModelRepository(tempModelName, repositoriesList);
                    fileWriter.write("  @Autowired\n");
                    fileWriter.write("  private "+repositoryName+" "+getClassObjectName(repositoryName)+";\n");
                    fileWriter.write("  private Iterable<"+tempModelName+"> "+getClassObjectName(tempModelName)+"Snapshot;\n");
                }

            }
            fileWriter.write("  private "+modelName+" current"+modelName+" = new "+modelName+"();\n\n");
            String modelNameLowerCase = modelName.toLowerCase();

            // creating method
            writeStepDefinitionMethodDeleteExisting(fileWriter, modelName, modelNameLowerCase, repositoryObjectName);

            // creating method
            writeStepDefinitionMethodCreateModelWithValues(fileWriter, fields, methods, repositoryObjectName, modelName, modelNameLowerCase, repositoriesList);

            // creating method
            writeMethodGetModelById(fileWriter, modelName, repositoryObjectName);

            // creating method
            writeStepDefinitionMethodSaveDatabaseSnapshot(fileWriter, modelNameLowerCase, modelsList, repositoriesList);

            // creating method
            writeMethodAssertEntityEquals(fileWriter);

            // creating method
            writeMethodAssertRemainingEntries(fileWriter);

            // creating method
            writeMethodAssertModelObjectEquals(fileWriter, modelName, modelNameLowerCase, relationalFields);

            // creating method
            writeMethodAssertOtherEntitiesUnchanged(fileWriter, modelsList, repositoriesList, modelName);

            // creating method
            writeMethodAssertCreationStatusWithSnapshotValidation(fileWriter, modelName, modelNameLowerCase, relationalFields);

            // creating method
            writeMethodAssertSnapshotUnchanged(fileWriter, modelNameLowerCase, relationalFields);

            // creating method
            writeStepDefinitionMethodCheckSingleModelCreation(fileWriter, fields, methods, modelName, modelNameLowerCase, repositoryObjectName);

            // creating method
            writeStepDefinitionMethodCheckModelCreation(fileWriter, fields, methods, modelName, modelNameLowerCase);

            // creating method
            writeStepDefinitionMethodCheckFetchingModel(fileWriter, modelName, modelNameLowerCase);

            // creating method
            writeMethodAssertDeletionStatusWithSnapshotValidation(fileWriter, modelNameLowerCase, relationalFields);

            // creating method
            writeStepDefinitionMethodCheckDeletingModel(fileWriter, modelName, modelNameLowerCase, repositoryObjectName);

            fileWriter.write("}");
        }
        else
            fileWriter.write("// Could not locate repository for model: " + modelName);

        fileWriter.flush();
        fileWriter.close();
    }
}

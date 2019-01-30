import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

interface ToDoListService {

    void saveList();
    void saveChangesToList();
    List<String> getNamesOfLists();
    String saveAs(ListDisplay listDisplay);
    ListDisplay setListDisplay(String fileName);
    ToDoList setToDoList(String text);
    ArrayList<String> getOpenLists();
    void setListAsOpen(String listName);
    void setNotOpen();
    void emailAsPDF(ListDisplay listDisplay);
    void emailAsObject(ListDisplay listDisplay);
}

public class ToDoList implements ToDoListService , Serializable {
    private static final long serialVersionUID=3041245930651489355L;
    private String fileName="To Do List";
    private ListDisplay listDisplay;
    private final String nameOfLists = "Names of lists";
    private final String openLists = "Names of open lists";

    public ToDoList(){
        try {
            Files.createFile(Paths.get("Names of lists"));
            Files.createFile(Paths.get("Names of open lists"));
        } catch (IOException ignored) {}
    }


    @Override
    public void saveList() {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(Files.newOutputStream(Paths.get(fileName+".ListDisplay")))){
            if (Files.readAllLines(Paths.get(nameOfLists)).contains(fileName)){
                JOptionPane.showMessageDialog(null,"That file name already exists. Please choose a new one.");
                saveAs(listDisplay);
            }else{
                objectOutputStream.writeObject(listDisplay);
                Files.writeString(Paths.get(nameOfLists),fileName+"\n",StandardOpenOption.APPEND);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(Files.newOutputStream(Paths.get(fileName+".ToDoList")))){
            objectOutputStream.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveChangesToList() {
        try {
            if (Files.readAllLines(Paths.get(nameOfLists)).contains(fileName)) {
                try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(Files.newOutputStream(Paths.get(fileName + ".ListDisplay")))) {
                    {
                        objectOutputStream.writeObject(listDisplay);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(Files.newOutputStream(Paths.get(fileName + ".ToDoList")))) {
                    objectOutputStream.writeObject(this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> getNamesOfLists() {
        try {
            return Files.readAllLines(Paths.get(nameOfLists));
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public String saveAs(ListDisplay listDisplay) {
        setNotOpen();
        do {
            fileName = JOptionPane.showInputDialog("Please enter the name of your ToDo list. (Cannot contain : \\ /" +
                    "* ? < > | \")");
            if (fileName == null)
                return "To Do List";
        } while (fileName.contains(":") || fileName.contains("\\") || fileName.contains("/") || fileName.contains("*")
                || fileName.contains("?") || fileName.contains("<") || fileName.contains(">") || fileName.contains("|")
                || fileName.contains("\""));
        if (fileName.equals(""))
            fileName = "new file";
        this.listDisplay = listDisplay;
        saveList();
        setListAsOpen(fileName);
        return fileName;
    }

    @Override
    public ListDisplay setListDisplay(String fileName) {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(Files.newInputStream(Paths.get(fileName+".ListDisplay")))) {
            return (ListDisplay) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ListDisplay();
        }
    }

    @Override
    public ToDoList setToDoList(String fileName) {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(Files.newInputStream(Paths.get(fileName+".ToDoList")))) {
            return (ToDoList) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ToDoList();
        }
    }

    @Override
    public ArrayList<String> getOpenLists() {
        ArrayList<String> list = new ArrayList<>();
        try {
            List listOfOpenLists = Files.readAllLines(Paths.get(openLists));
            list.addAll(listOfOpenLists);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void setListAsOpen(String listName) {
        try {
            Files.writeString(Paths.get(openLists),listName+"\n", StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setNotOpen() {
        try {
            int amount = 0;
            ArrayList<String> contents = getOpenLists();
            for (String s:contents) {
                if (s.equals(fileName))
                    amount++;
            }
            new PrintWriter(openLists).close();
            for (String s:contents) {
                if (!s.equals(fileName) || amount > 1)
                    setListAsOpen(s);
                if (s.equals(fileName))
                    amount--;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void emailAsPDF(ListDisplay listDisplay) {
        new Email(listDisplay.getFrame() ,fileName,"pdf");
    }

    @Override
    public void emailAsObject(ListDisplay listDisplay) {
        saveBeforeSend();
        new Email(listDisplay.getFrame(), fileName,"object");
    }
    private void saveBeforeSend(){
        if (fileName.equalsIgnoreCase("To Do List"))
            saveAs(listDisplay);
        else
            saveChangesToList();
    }
}

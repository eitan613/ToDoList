import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Stack;

public class ListDisplay implements Serializable {

    private JFrame frame = new JFrame("To Do List");
    private JPanel panel = new JPanel();
    private JButton addItem = new JButton("Add item");
    private ToDoList list = new ToDoList();
    private Stack<String> removed = new Stack<>();
    private JButton undo = new JButton();
    private JMenuBar menuBar = new JMenuBar();
    private JMenu menu = new JMenu("Menu");
    private JMenuItem newList = new JMenuItem("New");
    private JMenuItem saveAs = new JMenuItem("Save As");
    private JMenuItem open = new JMenuItem("Open");
    private JMenu submenu = new JMenu("share");
    private static final long serialVersionUID=7616818199942032576L;

    public ListDisplay(){
        setFrame();
        setPanel();
        setMenu();
        setAddButton();
        setUndoButton();
        list.setListAsOpen(frame.getTitle());
        frame.setVisible(true);
    }

    public JFrame getFrame() {
        return frame;
    }

    private class NewListActionListener implements ActionListener, Serializable{

        private static final long serialVersionUID = -2099506922560777498L;

        @Override
        public void actionPerformed(ActionEvent e) {
            new ListDisplay();
        }
    }
    private class SaveAsActionListener implements ActionListener, Serializable{

        private static final long serialVersionUID = 7936068887207317153L;

        @Override
        public void actionPerformed(ActionEvent e) {
            frame.setTitle(list.saveAs(ListDisplay.this));
        }
    }

    private class OpenButtonActionListener implements ActionListener, Serializable{

        private static final long serialVersionUID = 7819019316313462201L;

        @Override
        public void actionPerformed(ActionEvent e) {
            JFrame listOfLists = new JFrame();
            listOfLists.setSize(250,400);
            listOfLists.setLayout(new GridLayout(0,1,0,1));
            ArrayList<String> namesOfLists = new ArrayList<>(list.getNamesOfLists());
            for (String name:namesOfLists) {
                JButton button = new JButton(name);
                button.addActionListener(b -> {
                    if (!list.getOpenLists().contains(button.getText())){ //can't have multiple versions of same list open. would lead to issues.
                        ListDisplay newListDisplay = list.setListDisplay(button.getText());
                        newListDisplay.frame.setTitle(button.getText());
                        newListDisplay.list = ListDisplay.this.list.setToDoList(button.getText());
                        newListDisplay.frame.setVisible(true);
                        list.setListAsOpen(button.getText());
                    }
                    listOfLists.dispose();
                });
                listOfLists.add(button);
            }
            listOfLists.setVisible(true);
        }
    }
    private void setMenu() {
        menuBar.add(menu);
        newList.addActionListener(new NewListActionListener());
        menu.add(newList);
        saveAs.addActionListener(new SaveAsActionListener());
        menu.add(saveAs);
        open.addActionListener(new OpenButtonActionListener());
        menu.add(open);
        setSubmenu();
        menu.add(submenu);
        frame.setJMenuBar(menuBar);
    }

    private void setSubmenu() {
        JMenuItem pdf = new JMenuItem("Share as pdf");
        pdf.addActionListener(e -> {list.emailAsPDF(this);});
        JMenuItem asObject = new JMenuItem("Share object");
        asObject.addActionListener(e -> {list.emailAsObject(this);});
        submenu.add(pdf);
        submenu.add(asObject);
    }

    private class UndoButtonActionListener implements ActionListener, Serializable{

        private static final long serialVersionUID = -6570611192638964968L;

        @Override
        public void actionPerformed(ActionEvent e) {
            updateList(removed.pop());
            if (removed.empty())
                undo.setEnabled(false);
        }
    }
    private void setUndoButton() {
        ImageIcon image = new ImageIcon("images\\undo.png");
        Image pic = image.getImage();
        Image newimg = pic.getScaledInstance(30,30, Image.SCALE_SMOOTH);
        image = new ImageIcon(newimg);
        undo.setIcon(image);
        undo.setBackground(Color.BLACK);
        undo.setEnabled(false);
        undo.addActionListener(new UndoButtonActionListener());
    }

    private class MyFrameListener extends WindowAdapter implements Serializable{

        private static final long serialVersionUID = -3138524906401820564L;

        @Override
        public void windowClosing(WindowEvent e) {
            list.saveChangesToList();
            ArrayList openLists = list.getOpenLists();
            list.setNotOpen();
            if(openLists.size() == 1 && openLists.get(0).equals(frame.getTitle())){
                System.exit(0);
            }
        }
    }
    private void setFrame() {
        frame.setSize(400,400);
        frame.addWindowListener(new MyFrameListener());
        frame.add(addItem,BorderLayout.PAGE_START);
        frame.add(undo,BorderLayout.PAGE_END);
        frame.add(panel,BorderLayout.CENTER);
    }

    private void setPanel() {
        panel.setLayout(new GridLayout(0,1,0,1));
        panel.setBackground(Color.DARK_GRAY);
    }

    private class AddButtonActionListener implements ActionListener, Serializable{

        private static final long serialVersionUID = -2758504554847655025L;

        @Override
        public void actionPerformed(ActionEvent e) {
            String itemText = JOptionPane.showInputDialog(frame,"Please enter the new item for your ToDo list:");
            if (itemText != null)
                updateList(itemText);
        }
    }

    private void setAddButton() {
        addItem.addActionListener(new AddButtonActionListener());
    }

    private void updateList(String newItem){
        ToDoItem item = new ToDoItem();
        item.setText(newItem);
        item.setItemStatus(ToDoItem.ItemStatus.REGULAR_PRIORITY);
        setNewButton(item);
        panel.revalidate();
        panel.repaint();
    }
    private class ToDoItemActionListener implements ActionListener, Serializable{
        private static final long serialVersionUID = -1156767608316237326L;
        private final JButton itemDisplay;
        private ToDoItem item;

        private ToDoItemActionListener(JButton itemDisplay, ToDoItem item){
            this.itemDisplay = itemDisplay;
            this.item = item;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            JFrame tempFrame = new JFrame(item.getText());
            JPanel tempPanel = new JPanel();
            tempFrame.add(tempPanel);
            JButton remove = new JButton("Remove this item");
            JButton edit = new JButton("Edit this item");
            JButton done = new JButton("Mark Done");
            JButton urgency = new JButton("Mark urgent");
            tempPanel.setLayout(new GridLayout(0,1,0,1));
            tempFrame.setSize(220,200);
            tempPanel.add(edit);
            tempPanel.add(urgency);
            tempPanel.add(done);
            tempPanel.add(remove);
            tempFrame.setVisible(true);

            remove.addActionListener(r -> {
                removed.push(item.getText());
                if (!undo.isEnabled())
                    undo.setEnabled(true);
                panel.remove(itemDisplay);
                panel.revalidate();
                panel.repaint();
                tempFrame.dispose();
            });
            edit.addActionListener(ed -> {
                itemDisplay.setText(JOptionPane.showInputDialog(frame,"Please type new text for this itemDisplay on your list:"));
                item.setText(itemDisplay.getText());
            });
            done.addActionListener(d -> {
                if (item.getItemStatus()!= ToDoItem.ItemStatus.COMPLETED){
                    setItemStatus(ToDoItem.ItemStatus.COMPLETED,Color.WHITE);
                    done.setText("Mark Uncompleted");
                    itemDisplay.setEnabled(false);
                    itemDisplay.setIcon(new ImageIcon("images\\done.gif"));
                }
                else{
                    setItemStatus(ToDoItem.ItemStatus.REGULAR_PRIORITY,Color.LIGHT_GRAY);
                    done.setText("Mark Done");
                    itemDisplay.setEnabled(true);
                    itemDisplay.setIcon(null);
                }
            });
            urgency.addActionListener(e14 -> {
                if (item.getItemStatus() != ToDoItem.ItemStatus.URGENT){
                    setItemStatus(ToDoItem.ItemStatus.URGENT,Color.RED);
                    urgency.setText("Set to regular priority");
                }
                else if (item.getItemStatus() == ToDoItem.ItemStatus.URGENT){
                    setItemStatus(ToDoItem.ItemStatus.REGULAR_PRIORITY,Color.LIGHT_GRAY);
                    urgency.setText("Mark Urgent");
                }
            });
        }

        private void setItemStatus(ToDoItem.ItemStatus status, Color c) {
            item.setItemStatus(status);
            itemDisplay.setBackground(c);
        }
    }

    private void setNewButton(ToDoItem item) {
        JButton itemDisplay = new JButton(item.getText());
        itemDisplay.setBorderPainted(false);
        itemDisplay.setBackground(Color.LIGHT_GRAY);
        itemDisplay.addActionListener(new ToDoItemActionListener(itemDisplay, item));
        panel.add(itemDisplay);
    }
}
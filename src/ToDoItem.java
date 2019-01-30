import java.io.Serializable;

public class ToDoItem implements Serializable {
    public enum ItemStatus {REGULAR_PRIORITY, URGENT, COMPLETED}

    private String text;
    private ItemStatus itemStatus;

    public void setText(String text) {
        this.text = text;
    }
    public String getText(){
        return text;
    }
    public void setItemStatus(ItemStatus status){
        itemStatus = status;
    }
    public ItemStatus getItemStatus(){
        return itemStatus;
    }
}

import javax.swing.*;
import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;

class PrintableToDoList implements Printable
{
    private JFrame frame;
    public PrintableToDoList(JFrame frame){
        this.frame=frame;
    }
    public int print (Graphics g, PageFormat pf, int pageIndex)
    {
        if (pageIndex == 0)
        {
            g.translate(100, 100);
            frame.revalidate();
            frame.repaint();
            frame.print(g);
            return Printable.PAGE_EXISTS;
        }
        else
        {
            return Printable.NO_SUCH_PAGE;
        }
    }
}
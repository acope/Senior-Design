package odrive;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OController implements ActionListener{
    private OView view;
    
    public OController(){
        view = new OView();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

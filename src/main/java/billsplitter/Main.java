
package billsplitter;

import billsplitter.service.DataStore;
import billsplitter.ui.HomeFrame;

public class Main {
    public static void main(String[] args) {

        DataStore.loadData();

        new HomeFrame().setVisible(true);
        
    }
}


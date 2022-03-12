
import java.awt.*;
import javax.swing.*;
import javax.sound.midi.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.awt.event.*;

//https://www.codejava.net/java-se/swing/jframe-basic-tutorial-and-examples
public class BeatBox {
    JPanel mainPanel;
    ArrayList<JCheckBox> checkBoxList;
    Sequencer sequencer;
    Sequence sequence;
    Track track;
    JFrame theFrame;

    String[] instrumentNames = {"Bass Drum", "Closed Hi-Hat", "Open Hi-Hat", "Acoustic Snare", "Crash Cymbal", "Hand Clap", "High Tom", "Hi Bongo", "Maracas", "Whistle", "Low Conga", "Cowbell", "Vibraslap", "Low-mid Tom", "High Agogo", "Open Hi Conga"};
    int[] instruments = {35, 42, 46, 38, 49, 39, 50, 60, 70, 72, 64, 56, 58, 47, 67, 63};

    public static void main(String[] args) {
        new BeatBox().buildGUI();
    }

    public void buildGUI() {
        theFrame = new JFrame("Cyber BeatBox by Pavel Vasilevich");
        theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BorderLayout layout = new BorderLayout();
        JPanel background = new JPanel(layout);
        background.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        checkBoxList = new ArrayList<JCheckBox>();
        Box buttonBox = new Box(BoxLayout.Y_AXIS);

        JButton start = new JButton("Start Pavel");
        start.addActionListener(new MyStartListener());
        buttonBox.add(start);

        JButton stop = new JButton("Stop");
        stop.addActionListener(new MyStopListener());
        buttonBox.add(stop);

        JButton upTempo = new JButton("Tempo Up");
        upTempo.addActionListener(new MyUpTempoListener());
        buttonBox.add(upTempo);

        JButton downTempo = new JButton("Tempo Down");
        downTempo.addActionListener(new MyDownTempoListener());
        buttonBox.add(downTempo);


        JButton serialize = new JButton("Serialize track");
        serialize.addActionListener(new MySendListener());
        buttonBox.add(serialize);

        JButton load = new JButton("Load");
        load.addActionListener(new MyReadInListener());
        buttonBox.add(load);


        JButton saveF = new JButton("Save to file");
        saveF.addActionListener(new MySaveFListener());
        buttonBox.add(saveF);

        JButton loadF = new JButton("Load from file");
        loadF.addActionListener(new MyLoadFListener());
        buttonBox.add(loadF);

        JButton aboutBt = new JButton("About");
        aboutBt.addActionListener(//);
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        //dispose();
                        JFrame aboutWindow = new JFrame("About Window");
                        BorderLayout layout = new BorderLayout();
                        JPanel background = new JPanel(layout);
                        background.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                        Box buttonBox = new Box(BoxLayout.Y_AXIS);

                        JLabel label = new JLabel("This app from Pavel,", SwingConstants.CENTER);
                        JLabel label1 = new JLabel("another app here:", SwingConstants.CENTER);
                        JLabel label2 = new JLabel(
                                "https://play.google.com/store/apps/developer?id=Pavel+Vasilevich&hl=ru&gl=US",
                                SwingConstants.CENTER);

                        //"\n " +
                        //"\n https://play.google.com/store/apps/developer?id=Pavel+Vasilevich&hl=ru&gl=US");

                        JLabel hyperlink = new JLabel("Visit Google Play");
                        hyperlink.setForeground(Color.BLUE.darker());
                        hyperlink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        hyperlink.addMouseListener(new MouseAdapter() {

                            @Override
                            public void mouseClicked(MouseEvent e) {
                                // the user clicks on the label
                                try {

                                    Desktop.getDesktop().browse(new URI(
                                            "https://play.google.com/store/apps/developer?id=Pavel+Vasilevich&hl=ru&gl=US"));

                                } catch (IOException | URISyntaxException e1) {
                                    e1.printStackTrace();
                                }
                            }

                            @Override
                            public void mouseEntered(MouseEvent e) {
                                // the mouse has entered the label
                                hyperlink.setText("<html><a href=''>Visit Google Play</a></html>");
                            }

                            @Override
                            public void mouseExited(MouseEvent e) {
                                // the mouse has exited the label
                                // https://www.codejava.net/java-se/swing/how-to-create-hyperlink-with-jlabel-in-java-swing+
                                hyperlink.setText("Visit Google Play");
                            }
                        });


                        buttonBox.add(label);
                        buttonBox.add(label1);
                        buttonBox.add(hyperlink);
                        aboutWindow.add(buttonBox);
                        //aboutWindow.add(label);

                        aboutWindow.setSize(300, 100);
                        aboutWindow.setResizable(false);
                        aboutWindow.setVisible(true);
                    }
                });
        buttonBox.add(aboutBt);


        Box nameBox = new Box(BoxLayout.Y_AXIS);
        for (int i = 0; i < 16; i++) {
            nameBox.add(new Label(instrumentNames[i]));
        }

        background.add(BorderLayout.EAST, buttonBox);
        background.add(BorderLayout.WEST, nameBox);

        theFrame.getContentPane().add(background);

        GridLayout grid = new GridLayout(16, 16);
        grid.setVgap(1);
        grid.setHgap(2);
        mainPanel = new JPanel(grid);
        background.add(BorderLayout.CENTER, mainPanel);

        for (int i = 0; i < 256; i++) {
            JCheckBox c = new JCheckBox();
            c.setSelected(false);
            checkBoxList.add(c);
            mainPanel.add(c);
        }

        setUpMidi();

        theFrame.setBounds(50, 50, 300, 300);
        theFrame.pack();
        theFrame.setVisible(true);
    }

    public void setUpMidi() {
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequence = new Sequence(Sequence.PPQ, 4);
            track = sequence.createTrack();
            sequencer.setTempoInBPM(120);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void buildTrackAndStart() {
        int[] trackList = null;

        sequence.deleteTrack(track);
        track = sequence.createTrack();

        for (int i = 0; i < 16; i++) {
            trackList = new int[16];

            int key = instruments[i];

            for (int j = 0; j < 16; j++) {
                JCheckBox jc = (JCheckBox) checkBoxList.get(j + (16 * i));
                if (jc.isSelected()) {
                    trackList[j] = key;
                } else {
                    trackList[j] = 0;
                }
            }
            makeTrack(trackList);
            track.add(makeEvent(176, 1, 127, 0, 16));
        }
        track.add(makeEvent(192, 9, 1, 0, 15));
        try {
            sequencer.setSequence(sequence);
            sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);
            sequencer.start();
            sequencer.setTempoInBPM(120);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class MyStartListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            buildTrackAndStart();
        }
    }

    public class MyStopListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            sequencer.stop();
        }
    }

    public class MyUpTempoListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float) (tempoFactor * 1.03));
        }
    }

    public class MyDownTempoListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float) (tempoFactor * .97));
        }
    }

    public class MySendListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            boolean[] checkboxState = new boolean[256];
            for (int i = 0; i < 256; i++) {
                JCheckBox check = (JCheckBox) checkBoxList.get(i);
                if (check.isSelected()) {
                    checkboxState[i] = true;
                }
            }
            try {
                FileOutputStream fileStream = new FileOutputStream(new File("Checkbox.ser"));
                ObjectOutputStream os = new ObjectOutputStream(fileStream);
                os.writeObject(checkboxState);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public class MyReadInListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            boolean[] checkboxState = null;
            try {
                FileInputStream fileIn = new FileInputStream(new File("Checkbox.ser"));
                ObjectInputStream is = new ObjectInputStream(fileIn);
                checkboxState = (boolean[]) is.readObject();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            for (int i = 0; i < 256; i++) {
                JCheckBox check = (JCheckBox) checkBoxList.get(i);
                if (checkboxState[i]) {
                    check.setSelected(true);
                } else {
                    check.setSelected(false);
                }
            }
            sequencer.stop();
            buildTrackAndStart();
        }
    }

    public class MySaveFListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {


////////////////
            JFileChooser fileSave = new JFileChooser();
            fileSave.showSaveDialog(theFrame);
            saveFile(fileSave.getSelectedFile());
/////////////

        }
    }

    private void saveFile(File file) {
        boolean[] checkboxState = new boolean[256];
        for (int i = 0; i < 256; i++) {
            JCheckBox check = (JCheckBox) checkBoxList.get(i);
            if (check.isSelected()) {
                checkboxState[i] = true;
            }
        }


        try {
            FileOutputStream fileStream = new FileOutputStream(file);
            ObjectOutputStream os = new ObjectOutputStream(fileStream);
            os.writeObject(checkboxState);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public class MyLoadFListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            JFileChooser fileOpen = new JFileChooser();
            fileOpen.showOpenDialog(theFrame);
            loadFile(fileOpen.getSelectedFile());

        }
    }

    private void loadFile(File file) {
        boolean[] checkboxState = null;
        try {
            FileInputStream fileSt = new FileInputStream(file);
            ObjectInputStream is = new ObjectInputStream(fileSt);
            checkboxState = (boolean[]) is.readObject();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        for (int i = 0; i < 256; i++) {
            JCheckBox check = (JCheckBox) checkBoxList.get(i);
            if (checkboxState[i]) {
                check.setSelected(true);
            } else {
                check.setSelected(false);
            }
        }
        sequencer.stop();
        buildTrackAndStart();

    }

    public void makeTrack(int[] list) {
        for (int i = 0; i < 16; i++) {
            int key = list[i];
            if (key != 0) {
                track.add(makeEvent(144, 9, key, 100, i));
                track.add(makeEvent(128, 9, key, 100, i + 1));
            }
        }
    }

    public MidiEvent makeEvent(int comd, int chan, int one, int two, int tick) {
        MidiEvent event = null;
        try {
            ShortMessage a = new ShortMessage();
            a.setMessage(comd, chan, one, two);
            event = new MidiEvent(a, tick);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return event;
    }
}

//https://play.google.com/store/apps/developer?id=Pavel+Vasilevich&hl=ru&gl=US
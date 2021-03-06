package view;

import model.WrongFormatException;
import presenter.DataReader;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

public class Start {
    private JPanel pnl_start;
    private JTextField txtField_selectedFile;
    private JButton browseButton;
    private JButton okButton;
    private JLabel lb_error;
    private JButton helpButton;
    private JLabel lb_title;
    private JLabel lb_110icon;

    public Start() {
        browseButton.addActionListener(new BrowseBtnClicked());
        okButton.addActionListener(new OKBtnClicked());
        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HelpDialog h = new HelpDialog();
                h.showDialog(false);
            }
        });
    }



    private class BrowseBtnClicked implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fc = new JFileChooser();
            Preferences p = Preferences.userRoot().node(this.getClass().getName());
            String saved = p.get("lastPath", null);

            if (saved != null)
                fc.setCurrentDirectory(new File(saved));
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "CSV File", "csv");
            fc.setFileFilter(filter);

            int option = fc.showOpenDialog(pnl_start);

            if (option == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fc.getSelectedFile();
                String dir = selectedFile.getParent();

                p.put("lastPath", dir);
                
                String path = selectedFile.getPath();

                txtField_selectedFile.setText(path);

            }

        }
    }

    public JPanel getJPanel() {
        return pnl_start;
    }

    private class OKBtnClicked implements  ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            lb_error.setForeground(Color.BLACK);

            SwingWorker<Void, Void> worker  = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    String path = txtField_selectedFile.getText();

                    if (!path.equals("")) {
                        try {

                            SwingUtilities.invokeLater(() -> {
                                lb_error.setText("Reading file...");

                            });

                            DataReader.readData(path);
                            SwingUtilities.invokeLater(() -> {
                                lb_error.setText("Done");
                                MainMenu m = new MainMenu();
                                JFrame frame = Main.m;
                                frame.setContentPane(m.getJPanel());
                                frame.setLocationRelativeTo(null);
                                frame.pack();
                            });

                        } catch (WrongFormatException e1) {
                            e1.printStackTrace();
                            SwingUtilities.invokeLater(() -> {
                                lb_error.setForeground(Color.RED);
                                lb_error.setText("CSV file has wrong format");
                            });


                        } catch (IOException e1) {
                            e1.printStackTrace();
                            SwingUtilities.invokeLater(() -> {
                                lb_error.setForeground(Color.RED);
                                lb_error.setText("IO Exception occured");
                            });
                        }
                    }

                    else
                        SwingUtilities.invokeLater(() -> {
                            lb_error.setForeground(Color.RED);
                            lb_error.setText("Select a file");
                        });
                    return null;
                }
            };

            worker.execute();

        }
    }
}

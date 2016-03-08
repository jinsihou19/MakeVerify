import javax.swing.*;
import javax.swing.plaf.UIResource;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.im.InputContext;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by jinsihou on 15/11/3.
 */
public class UI {
    JFrame jFrame = null;
    JPanel jPanel = null;
    JPanel filePanel = null;
    JPanel centerPanel = null;
    JTextField tips = null;
    JTextField selectFilePath = null;
    JTextArea outPanel = null;
    JButton MD5Btn = null;
    JButton selectBtn = null;

    public UI() throws UnsupportedEncodingException {
        jFrame = new JFrame("MD5计算器");
        jPanel = new JPanel(new BorderLayout());
        tips = new JTextField("请选择文件:", 1);
        MD5Btn = new JButton("MD5");
        centerPanel = new JPanel(new BorderLayout());
        outPanel = new JTextArea();
        filePanel = new JPanel(new BorderLayout());
        selectFilePath = new JTextField(1);
        selectBtn = new JButton("选择");

        jFrame.setSize(400, 300);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        tips.setEditable(false);
        //接受拖放文件
        outPanel.setDragEnabled(true);
        outPanel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        outPanel.setTransferHandler(new DefaultTransferHandler());

        filePanel.add(selectFilePath, BorderLayout.CENTER);
        filePanel.add(selectBtn, BorderLayout.EAST);
        centerPanel.add(filePanel, BorderLayout.NORTH);
        centerPanel.add(outPanel, BorderLayout.CENTER);
        selectBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jfc = new JFileChooser();
                jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                jfc.showDialog(new JLabel(), "打开");
                File file = jfc.getSelectedFile();
                if (file.isDirectory()) {
                    selectFilePath.setText(file.getAbsolutePath());
                } else if (file.isFile()) {
                    selectFilePath.setText(file.getAbsolutePath());
                }
                outPanel.setText("当前选择的文件是：" + file.getName() + "\n");
            }
        });
        MD5Btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fileName = selectFilePath.getText();
                if (!fileName.equals("")) {
                    String result = MD5Algorithmic.getFileMD5(new File(fileName));
                    outPanel.append("MD5:" + result + "\n");
                }
            }
        });
        jPanel.add(tips, BorderLayout.NORTH);
        jPanel.add(centerPanel);
        jPanel.add(MD5Btn, BorderLayout.SOUTH);

        jFrame.add(jPanel);
        jFrame.setVisible(true);
    }

    class DefaultTransferHandler extends TransferHandler implements
            UIResource {

        public void exportToClipboard(JComponent comp, Clipboard clipboard,
                                      int action) throws IllegalStateException {
            if (comp instanceof JTextComponent) {
                JTextComponent text = (JTextComponent) comp;
                int p0 = text.getSelectionStart();
                int p1 = text.getSelectionEnd();
                if (p0 != p1) {
                    try {
                        Document doc = text.getDocument();
                        String srcData = doc.getText(p0, p1 - p0);
                        StringSelection contents = new StringSelection(srcData);

                        // this may throw an IllegalStateException,
                        // but it will be caught and handled in the
                        // action that invoked this method
                        clipboard.setContents(contents, null);

                        if (action == TransferHandler.MOVE) {
                            doc.remove(p0, p1 - p0);
                        }
                    } catch (BadLocationException ble) {
                    }
                }
            }
        }

        public boolean importData(JComponent comp, Transferable t) {
            try {
                if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    java.util.List<File> fileList = (java.util.List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
                    File file = fileList.get(0);
                    if (file != null) {
                        String fileName = file.getAbsolutePath();
                        selectFilePath.setText(fileName);
                        String result = MD5Algorithmic.getFileMD5(file);
                        outPanel.append("MD5:" + result + "\n");
                    }
                    return true;
                }
            } catch (UnsupportedFlavorException ufe) {
                ufe.printStackTrace();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }

            DataFlavor flavor = getFlavor(t.getTransferDataFlavors());

            if (flavor != null) {
                InputContext ic = comp.getInputContext();
                if (ic != null) {
                    ic.endComposition();
                }
                try {
                    String data = (String) t.getTransferData(flavor);

                    ((JTextComponent) comp).replaceSelection(data);
                    return true;
                } catch (UnsupportedFlavorException ufe) {
                } catch (IOException ioe) {
                }
            }
            return false;
        }

        public boolean canImport(JComponent comp,
                                 DataFlavor[] transferFlavors) {
//            JTextComponent c = (JTextComponent) comp;
//            if (!(c.isEditable() && c.isEnabled())) {
//                return false;
//            }
//            return (getFlavor(transferFlavors) != null);
            return true;
        }

        public int getSourceActions(JComponent c) {
            return NONE;
        }

        private DataFlavor getFlavor(DataFlavor[] flavors) {
            if (flavors != null) {
                for (DataFlavor flavor : flavors) {
                    if (flavor.equals(DataFlavor.stringFlavor)) {
                        return flavor;
                    }
                }
            }
            return null;
        }
    }
}

package Test;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTable;

import java.awt.BorderLayout;
import java.awt.Desktop;

import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import javax.swing.JTextField;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class Explorer {

	private JFrame frame;
	private JTextField tf_path;
	public static String ROOT ="D:\\";
	public static String CURRENT = ROOT,PREVIOUS;
	public static String CURRENT_TREE = ROOT;
	public String treechild="";
	private JTable DataTable;
	public String FOLDER_HEADER[] = new String[] { "Name", "Date modified", "Type",
    "Size"};
	public DefaultTableModel TABLE_MODEL = new DefaultTableModel(0, 0);
	public File folder,fileRoot = new File(ROOT);
	public File[] list;
	private JPanel panel;
	private JButton btnCopy;
	private JButton btnPaste;
	private String[] sourcePath,lastName;
	private String destPath,lastPath,currentPath=ROOT;
	private String[] multiSourcePath,multiSourceLastPath;
	private JButton btnDelete;
	private JButton btnBack;
	private JButton btnOpen;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Explorer window = new Explorer();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Explorer() {
		initialize();
	}
	

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 669, 371);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JScrollPane scrollPane = new JScrollPane();
		frame.getContentPane().add(scrollPane, BorderLayout.EAST);
		
		JPanel topPanel = new JPanel();
		frame.getContentPane().add(topPanel, BorderLayout.NORTH);
		
		btnBack = new JButton();
//		btnBack.setIcon(new ImageIcon(Explorer.class.getResource("/com/sun/javafx/scene/web/skin/Undo_16x16_JFX.png")));
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				TABLE_MODEL = loadFile(PREVIOUS, TABLE_MODEL);
				tf_path.setText(PREVIOUS);
			}
		});
		topPanel.add(btnBack);
		
		tf_path = new JTextField();
		topPanel.add(tf_path);
		tf_path.setColumns(50);
		tf_path.setText(CURRENT);
		
		//Jtable Data
		DataTable = new JTable();
		DataTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				loadDataTable(e);
			}
		});
		scrollPane.setViewportView(DataTable);
		DataTable.setModel(TABLE_MODEL);
		DataTable.setRowSelectionAllowed(true);
		DataTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		TABLE_MODEL.setColumnIdentifiers(FOLDER_HEADER);
		TABLE_MODEL = loadFile(CURRENT, TABLE_MODEL);
		DataTable.setAutoCreateRowSorter(true);
		
		//JTree
		JTree tree = new JTree();
		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				treeAction(arg0);
			}
		});
		    
		DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(FileNode(fileRoot));
		DefaultTreeModel treemodel = new DefaultTreeModel(treeNode);
		tree.setModel(treemodel);
		createChildren(fileRoot, treeNode);
		tree.setShowsRootHandles(true);
		
		JScrollPane treescroll = new JScrollPane(tree);
		frame.getContentPane().add(treescroll, BorderLayout.CENTER);
		
		panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.SOUTH);

		btnCopy = new JButton("Copy");
		btnCopy.setVerticalAlignment(SwingConstants.BOTTOM);
		btnCopy.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				copyFile();
			}
		});
		panel.add(btnCopy);
		
		btnPaste = new JButton("Paste");
		btnPaste.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				pasteFile();
			}
		});
		panel.add(btnPaste);
		
		btnDelete = new JButton("Delete");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int i = JOptionPane.showConfirmDialog(btnDelete, "Do you want to delete this files ?", "Delete Dialog",1);
				if (i == 0) {
					deleteFile();
				}
			}
		});
		panel.add(btnDelete);
		
		btnOpen = new JButton("Open");
		btnOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Desktop desktop = Desktop.getDesktop();
				try {
					desktop.open(new File(tf_path.getText()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		panel.add(btnOpen);
		
	}
	
	//Bắt sự kiện cho click vào Data trên Table
	public void loadDataTable(MouseEvent e) {
		JTable table=(JTable) e.getSource();
		int row = table.getSelectedRow();
		int[] multirow = table.getSelectedRows();
		multiSourcePath = new String[multirow.length];
		multiSourceLastPath = new String[multirow.length];
		for(int i = 0 ; i<multirow.length;i++) {
			multiSourcePath[i] = currentPath+table.getModel().getValueAt(multirow[i], 0)+"\\";
			multiSourceLastPath[i] = table.getValueAt(multirow[i], 0).toString();
		}
		String name = table.getModel().getValueAt(row, 0).toString();
		lastPath = name;
		CURRENT = tf_path.getText() + name +"\\";
		PREVIOUS = tf_path.getText();
		File folder = new File(CURRENT);
		if (folder.isDirectory()) {
			TABLE_MODEL = loadFile(CURRENT,TABLE_MODEL);
			currentPath = CURRENT;
			tf_path.setText(CURRENT);
		}else {
			lastPath = name;
			PREVIOUS = currentPath;
			tf_path.setText(currentPath+lastPath+"\\");
			
		}
	}
	
	//Load files cho Table
	public DefaultTableModel loadFile(String path,DefaultTableModel tableModel) {
		if (tableModel.getRowCount() > 0) {
			for (int i= tableModel.getRowCount()-1;i>-1;i--) {
				tableModel.removeRow(i);
			}
		}
		folder = new File(path);
		list = folder.listFiles();
		
		for (int i = 0; i < list.length; i++) {
			File current = list[i];
			String name = current.getName();
			String type = "";
			String size = "";
			if(list[i].isFile()) {
				type="File";
				long fileSize = (long) current.length() / 1024;
				size = " " +fileSize + "kB";
			}else {
				if (list[i].isDirectory()) {
					type="Folder";
				}
			}
			
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/YYY");
			String dateModified = simpleDateFormat.format(current.lastModified());
			tableModel.addRow(new Object[] {name,dateModified,type,size});
		}
		return tableModel;
	}
	//Tạo nhánh tree con
	public void createChildren(File fileRoot,DefaultMutableTreeNode root) {
		File[] files = fileRoot.listFiles(); 
		if (files == null) {
			return;
		}
		for (File file : files) {
			DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(FileNode(file));
			root.add(childNode);
			if (file.isDirectory()) {
				createChildren(file, childNode);
			}
		}
	}
	//Bắt sự kiện khi click vào jtree
	public void treeAction(MouseEvent arg0) {
		JTree treemouse = (JTree) arg0.getSource();
		int treerow = treemouse.getLeadSelectionRow();
		String treename="";
		if (treemouse.getSelectionCount() != 0) {
			Object[] treepath = treemouse.getPathForRow(treerow).getPath();
			for (int i=0; i<treepath.length; i++) {
		        treename += treepath[i];
		        
		        if (i+1 <treepath.length) {
		            treename += File.separator;
		        }
			}
			if ((new File(treename)).isDirectory()) {
				treename = treename +"\\";
				TABLE_MODEL = loadFile(treename,TABLE_MODEL);
			}
			tf_path.setText(treename);
		}
	}
	
	public String FileNode(File file) {

	        this.fileRoot = file;

	        String name = file.getName();
	        if (name.equals("")) {
	            return file.getAbsolutePath();
	        } else {
	            return name;
	        }
	}
	public void copyFile() {
		sourcePath = new String[multiSourcePath.length];
		lastName = new String[multiSourcePath.length];
		for(int i=0;i<multiSourcePath.length;i++) {
			sourcePath[i] = multiSourcePath[i];
			lastName[i] = multiSourceLastPath[i];
		}
		for (String i : lastName) {
			System.out.print("CopyLastPath: "+i+" ");
		}
	}
	public void pasteFile() {
		String path = tf_path.getText();
		destPath = path;
		for (int i = 0; i < lastName.length; i++) {
			System.out.print("Copy From: "+sourcePath[i]+" ");
			System.out.println("Paste IN: "+destPath+lastName[i]);
		}
		for (int i = 0; i < sourcePath.length; i++) {
			try {
				Files.copy((new File(sourcePath[i])).toPath(), (new File(destPath+lastName[i])).toPath(),StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		TABLE_MODEL = loadFile(tf_path.getText(), TABLE_MODEL);
	}
	public void deleteFile() {
		try {
			Files.delete((new File(tf_path.getText()).toPath()));
			TABLE_MODEL = loadFile(PREVIOUS, TABLE_MODEL);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

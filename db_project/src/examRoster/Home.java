/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package examRoster;

import com.mysql.jdbc.*;
import java.awt.Font;
import java.awt.print.PrinterException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import javax.print.PrintException;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author      Md. Khairullah Gaurab
 * @co-author   Md. Mamun Hossain 
 */
public class Home extends javax.swing.JFrame {

    /**
     * This stores the exam_id of exam_roaster table to be deleted in the edit
     * EditRoutine Panel
     */
    public int examIDtoDel;
    public Vector<String> temp;

    private void ResetTheList() {
        DBConnection Dobj = new DBConnection();
        Connection connect = (Connection) Dobj.dbConnect(DBConnection.dataBaseUrl, DBConnection.dataBaseUserName, DBConnection.dataBasePassWord);
        Statement statement = null;
        ResultSet resultset = null;
        Vector<String> temp = new Vector<String>();
        String query = "SELECT * FROM exam_roster join course using(course_id)";
        try {
            statement = (Statement) connect.createStatement();
            resultset = statement.executeQuery(query);
            while (resultset.next()) {
                temp.add(String.format("%s   %s    %-50s    %10s    %10s    %10s    %10s    %10s\n", resultset.getString(2), resultset.getString(6), resultset.getString(7), resultset.getString(8), resultset.getString(9), resultset.getString(3), resultset.getString(4), resultset.getString(5)));
            }
            statement.close();
            resultset.close();
            connect.close();
        } catch (SQLException e) {

        }
        DefaultListModel DML = new DefaultListModel();
        temp.stream().forEach((string) -> {
            DML.addElement(string);
        });
        RoutineList.setModel(DML);
        RoutineList.setFont(font);
    }

    private void GenerateDutyList() {
        DBConnection Dobj = new DBConnection();
        Connection connect = (Connection) Dobj.dbConnect(DBConnection.dataBaseUrl, DBConnection.dataBaseUserName, DBConnection.dataBasePassWord);
        Statement st1 = null, st2 = null;
        ResultSet rst1 = null, rst2 = null;
        String query1, query2;
        query1 = "SELECT * FROM exam_roster";
        query2 = "SELECT * FROM priority_table order by priority_no desc";
        try {
            st1 = (Statement) connect.createStatement();
            rst1 = st1.executeQuery(query1);
            while (rst1.next()) {
                int exID = rst1.getInt(1);
                st2 = (Statement) connect.createStatement();
                rst2 = st2.executeQuery(query2);
                while (rst2.next()) {
                    int tID = rst2.getInt(2);
                    int gduty = rst2.getInt(4);
                    int mduty = rst2.getInt(5);
                    if (mduty - gduty > 0) {
                        ((Statement) connect.createStatement()).executeUpdate(String.format("insert into teacher_assign (teacher_id,exam_id) values (%d,%d)", tID, exID));
                        ((Statement) connect.createStatement()).executeUpdate(String.format("update priority_table set given_duties = %d+1 where teacher_id = %d", gduty, tID));
                        break;
                    }
                }
                st2.close();
                rst2.close();
            }
            st1.close();
            rst1.close();
            connect.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    private void ResetTeacherDutyList(Connection connect) {
        Statement statement = null;
        ResultSet resultset = null;
        temp = new Vector<String>();
        String query = "select teacher_code, building_name, room_no, exam_time from teacher natural join exam_roster\n"
                + "where teacher_id in (select teacher_id from teacher_assign)";
        try {
            statement = (Statement) connect.createStatement();
            resultset = statement.executeQuery(query);
            if (resultset.next() == false) {
                GenerateDutyList();
                statement.close();
                resultset.close();
                statement = (Statement) connect.createStatement();
                resultset = statement.executeQuery(query);
            } else {
                resultset.previous();
            }
            while (resultset.next()) {
                temp.add(String.format("%-10s   %5s    %-10s   %10s\n", resultset.getString(1), resultset.getString(2), resultset.getString(3), resultset.getString(4)));
            }
            statement.close();
            resultset.close();
            connect.close();
        } catch (SQLException e) {
        }
        DefaultListModel DML = new DefaultListModel();
        temp.stream().forEach((string) -> {
            DML.addElement(string);
        });
        TeacherAssignList.setModel(DML);
        TeacherAssignList.setFont(font);
    }

    private void ResetTheList(Connection connect) {
        Statement statement = null;
        ResultSet resultset = null;
        Vector<String> temp = new Vector<String>();
        String query = "SELECT * FROM exam_roster join course using(course_id)";
        try {
            statement = (Statement) connect.createStatement();
            resultset = statement.executeQuery(query);
            while (resultset.next()) {
                temp.add(String.format("%s   %s    %-50s    %10s    %10s    %10s    %10s    %10s\n", resultset.getString(2), resultset.getString(6), resultset.getString(7), resultset.getString(8), resultset.getString(9), resultset.getString(3), resultset.getString(4), resultset.getString(5)));
            }
            statement.close();
            resultset.close();
            connect.close();
        } catch (SQLException e) {
        }
        DefaultListModel DML = new DefaultListModel();
        temp.stream().forEach((string) -> {
            DML.addElement(string);
        });
        RoutineList.setModel(DML);
        RoutineList.setFont(font);
    }

    private void DropFromTableExam() {
        DBConnection Dobj = new DBConnection();
        Connection connect = (Connection) Dobj.dbConnect(DBConnection.dataBaseUrl, DBConnection.dataBaseUserName, DBConnection.dataBasePassWord);
        Statement statement = null;
        ResultSet resultset = null;
        String query = "DELETE FROM exam_roster WHERE exam_id = ?";
        if (examIDtoDel == -1) {
            return;
        }
        try {
            PreparedStatement prest = (PreparedStatement) connect.prepareStatement(query);
            prest.setInt(1, examIDtoDel);
            int val = prest.executeUpdate();
            prest.close();
            connect.close();
        } catch (SQLException e) {

        }
    }

    private void FillCourseCombo() {
        jComboBox1.removeAllItems();
        jComboBox1.setFont(font);
        DBConnection Dobj = new DBConnection();
        Connection connect = (Connection) Dobj.dbConnect(DBConnection.dataBaseUrl, DBConnection.dataBaseUserName, DBConnection.dataBasePassWord);
        try {
            Statement st1 = null;
            ResultSet rst1 = null;
            String qr1 = "SELECT * from course";
            st1 = (Statement) connect.createStatement();
            rst1 = st1.executeQuery(qr1);
            while (rst1.next()) {
                jComboBox1.addItem(String.format("%s  %s   %-40s   %s   %s", rst1.getString(1), rst1.getString(2), rst1.getString(3), rst1.getString(4), rst1.getString(5)));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Nothing in course table!");
        }
    }

    /**
     * Creates new form Home
     */
    private final Font font = new Font("Monospaced", Font.PLAIN, 15);

    public Home() {

        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField1 = new javax.swing.JTextField();
        BackendPanel = new javax.swing.JPanel();
        HomeScreen = new javax.swing.JPanel();
        WelcomePanel = new javax.swing.JPanel();
        WelcomeLabel = new javax.swing.JLabel();
        NewrtBTN = new javax.swing.JButton();
        TDutyPanel = new javax.swing.JPanel();
        TeachersAssign = new javax.swing.JScrollPane();
        TeacherAssignList = new javax.swing.JList();
        TeacherCodejLabel = new javax.swing.JLabel();
        BuildingRoomjLabel = new javax.swing.JLabel();
        TimeOftheExamjLabel = new javax.swing.JLabel();
        printbtn2 = new javax.swing.JButton();
        RoutinePanel = new javax.swing.JPanel();
        RoutineInternalFrame = new javax.swing.JInternalFrame();
        RoutineScrollPane = new javax.swing.JScrollPane();
        RoutineTextArea = new javax.swing.JTextArea();
        printBtn = new javax.swing.JButton();
        EditRoutine = new javax.swing.JPanel();
        AddNewExam = new javax.swing.JButton();
        DropExam = new javax.swing.JButton();
        CurrentRoutine = new javax.swing.JScrollPane();
        RoutineList = new javax.swing.JList();
        AddNewExamPanel = new javax.swing.JPanel();
        AddThis = new javax.swing.JButton();
        jComboBox1 = new javax.swing.JComboBox();
        course_to_binser = new javax.swing.JLabel();
        Building_name_to_beinser = new javax.swing.JLabel();
        Building_textField = new javax.swing.JTextField();
        RoomNo_to_beinser = new javax.swing.JLabel();
        RoomNo_TextField = new javax.swing.JTextField();
        Date_to_beinser = new javax.swing.JLabel();
        Date_TextField = new javax.swing.JTextField();
        Time_to_beinser = new javax.swing.JLabel();
        Time_TextField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        Buttons = new javax.swing.JPanel();
        RoutineButton = new javax.swing.JButton();
        TDutyButton = new javax.swing.JButton();
        BackHomeButton = new javax.swing.JButton();

        jTextField1.setText("jTextField1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        HomeScreen.setLayout(new java.awt.CardLayout());

        WelcomeLabel.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        WelcomeLabel.setText("           Exam Roster Management System");

        NewrtBTN.setText("Create a New Routine");
        NewrtBTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NewrtBTNActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout WelcomePanelLayout = new javax.swing.GroupLayout(WelcomePanel);
        WelcomePanel.setLayout(WelcomePanelLayout);
        WelcomePanelLayout.setHorizontalGroup(
            WelcomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(WelcomePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(WelcomeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 909, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(WelcomePanelLayout.createSequentialGroup()
                .addGap(313, 313, 313)
                .addComponent(NewrtBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        WelcomePanelLayout.setVerticalGroup(
            WelcomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(WelcomePanelLayout.createSequentialGroup()
                .addGap(107, 107, 107)
                .addComponent(WelcomeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 192, Short.MAX_VALUE)
                .addComponent(NewrtBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        HomeScreen.add(WelcomePanel, "card2");

        TeacherAssignList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        TeacherAssignList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TeacherAssignListMouseClicked(evt);
            }
        });
        TeachersAssign.setViewportView(TeacherAssignList);

        TeacherCodejLabel.setText("Teacher Code");

        BuildingRoomjLabel.setText("Building and Room");

        TimeOftheExamjLabel.setText("Time of the Exam");

        printbtn2.setText("Print");
        printbtn2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printbtn2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout TDutyPanelLayout = new javax.swing.GroupLayout(TDutyPanel);
        TDutyPanel.setLayout(TDutyPanelLayout);
        TDutyPanelLayout.setHorizontalGroup(
            TDutyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TDutyPanelLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(TeacherCodejLabel)
                .addGap(76, 76, 76)
                .addComponent(BuildingRoomjLabel)
                .addGap(98, 98, 98)
                .addComponent(TimeOftheExamjLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 346, Short.MAX_VALUE)
                .addComponent(printbtn2, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(TDutyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(TDutyPanelLayout.createSequentialGroup()
                    .addGap(12, 12, 12)
                    .addComponent(TeachersAssign, javax.swing.GroupLayout.DEFAULT_SIZE, 823, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        TDutyPanelLayout.setVerticalGroup(
            TDutyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TDutyPanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(TDutyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TeacherCodejLabel)
                    .addComponent(BuildingRoomjLabel)
                    .addComponent(TimeOftheExamjLabel)
                    .addComponent(printbtn2))
                .addContainerGap(394, Short.MAX_VALUE))
            .addGroup(TDutyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(TDutyPanelLayout.createSequentialGroup()
                    .addGap(44, 44, 44)
                    .addComponent(TeachersAssign, javax.swing.GroupLayout.PREFERRED_SIZE, 348, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(45, Short.MAX_VALUE)))
        );

        HomeScreen.add(TDutyPanel, "card4");

        RoutineInternalFrame.setVisible(true);

        RoutineTextArea.setColumns(20);
        RoutineTextArea.setRows(5);
        RoutineScrollPane.setViewportView(RoutineTextArea);

        printBtn.setText("Print");
        printBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout RoutineInternalFrameLayout = new javax.swing.GroupLayout(RoutineInternalFrame.getContentPane());
        RoutineInternalFrame.getContentPane().setLayout(RoutineInternalFrameLayout);
        RoutineInternalFrameLayout.setHorizontalGroup(
            RoutineInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(RoutineInternalFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(RoutineInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(RoutineScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 891, Short.MAX_VALUE)
                    .addGroup(RoutineInternalFrameLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(printBtn))))
        );
        RoutineInternalFrameLayout.setVerticalGroup(
            RoutineInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(RoutineInternalFrameLayout.createSequentialGroup()
                .addComponent(printBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(RoutineScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout RoutinePanelLayout = new javax.swing.GroupLayout(RoutinePanel);
        RoutinePanel.setLayout(RoutinePanelLayout);
        RoutinePanelLayout.setHorizontalGroup(
            RoutinePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(RoutineInternalFrame, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        RoutinePanelLayout.setVerticalGroup(
            RoutinePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, RoutinePanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(RoutineInternalFrame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        HomeScreen.add(RoutinePanel, "card5");

        AddNewExam.setText("Add New");
        AddNewExam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddNewExamActionPerformed(evt);
            }
        });

        DropExam.setText("Drop Selected");
        DropExam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DropExamActionPerformed(evt);
            }
        });

        RoutineList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        RoutineList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                RoutineListMouseClicked(evt);
            }
        });
        CurrentRoutine.setViewportView(RoutineList);

        javax.swing.GroupLayout EditRoutineLayout = new javax.swing.GroupLayout(EditRoutine);
        EditRoutine.setLayout(EditRoutineLayout);
        EditRoutineLayout.setHorizontalGroup(
            EditRoutineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(EditRoutineLayout.createSequentialGroup()
                .addGap(254, 254, 254)
                .addComponent(AddNewExam, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(101, 101, 101)
                .addComponent(DropExam, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(185, Short.MAX_VALUE))
            .addGroup(EditRoutineLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(CurrentRoutine)
                .addContainerGap())
        );
        EditRoutineLayout.setVerticalGroup(
            EditRoutineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, EditRoutineLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(CurrentRoutine, javax.swing.GroupLayout.PREFERRED_SIZE, 348, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(EditRoutineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(AddNewExam, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DropExam, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27))
        );

        HomeScreen.add(EditRoutine, "card5");

        AddThis.setText("Add");
        AddThis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddThisActionPerformed(evt);
            }
        });

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        course_to_binser.setText("Course");

        Building_name_to_beinser.setText("Building");

        Building_textField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Building_textFieldActionPerformed(evt);
            }
        });

        RoomNo_to_beinser.setText("Room no.");

        RoomNo_TextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RoomNo_TextFieldActionPerformed(evt);
            }
        });

        Date_to_beinser.setText("Date:");

        Date_TextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Date_TextFieldActionPerformed(evt);
            }
        });

        Time_to_beinser.setText("Time");

        Time_TextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Time_TextFieldActionPerformed(evt);
            }
        });

        jLabel1.setText("HH:MM:SS");

        jLabel2.setText("YYYY-MM-DD");

        javax.swing.GroupLayout AddNewExamPanelLayout = new javax.swing.GroupLayout(AddNewExamPanel);
        AddNewExamPanel.setLayout(AddNewExamPanelLayout);
        AddNewExamPanelLayout.setHorizontalGroup(
            AddNewExamPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AddNewExamPanelLayout.createSequentialGroup()
                .addGroup(AddNewExamPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(AddNewExamPanelLayout.createSequentialGroup()
                        .addGap(254, 254, 254)
                        .addComponent(AddThis, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(AddNewExamPanelLayout.createSequentialGroup()
                        .addGap(80, 80, 80)
                        .addGroup(AddNewExamPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(AddNewExamPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(RoomNo_to_beinser, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(Building_name_to_beinser, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(Date_to_beinser)
                            .addComponent(Time_to_beinser)
                            .addComponent(course_to_binser))
                        .addGap(18, 18, 18)
                        .addGroup(AddNewExamPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 653, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(AddNewExamPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                                .addComponent(Time_TextField, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(Date_TextField, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(RoomNo_TextField, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(Building_textField, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addContainerGap(132, Short.MAX_VALUE))
        );
        AddNewExamPanelLayout.setVerticalGroup(
            AddNewExamPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AddNewExamPanelLayout.createSequentialGroup()
                .addGap(106, 106, 106)
                .addGroup(AddNewExamPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(course_to_binser)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(AddNewExamPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Building_name_to_beinser)
                    .addComponent(Building_textField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(AddNewExamPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(RoomNo_to_beinser)
                    .addComponent(RoomNo_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addGap(4, 4, 4)
                .addGroup(AddNewExamPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Date_to_beinser)
                    .addComponent(Date_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addComponent(jLabel1)
                .addGap(2, 2, 2)
                .addGroup(AddNewExamPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Time_to_beinser)
                    .addComponent(Time_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 102, Short.MAX_VALUE)
                .addComponent(AddThis, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27))
        );

        HomeScreen.add(AddNewExamPanel, "card5");

        RoutineButton.setText("Routine");
        RoutineButton.setMaximumSize(new java.awt.Dimension(134, 29));
        RoutineButton.setMinimumSize(new java.awt.Dimension(134, 29));
        RoutineButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RoutineButtonActionPerformed(evt);
            }
        });

        TDutyButton.setText("Teachers Duty");
        TDutyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TDutyButtonActionPerformed(evt);
            }
        });

        BackHomeButton.setText("Home");
        BackHomeButton.setMaximumSize(new java.awt.Dimension(134, 29));
        BackHomeButton.setMinimumSize(new java.awt.Dimension(134, 29));
        BackHomeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BackHomeButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ButtonsLayout = new javax.swing.GroupLayout(Buttons);
        Buttons.setLayout(ButtonsLayout);
        ButtonsLayout.setHorizontalGroup(
            ButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ButtonsLayout.createSequentialGroup()
                .addGap(156, 156, 156)
                .addComponent(RoutineButton, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 55, Short.MAX_VALUE)
                .addComponent(TDutyButton, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(59, 59, 59)
                .addComponent(BackHomeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48))
        );
        ButtonsLayout.setVerticalGroup(
            ButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ButtonsLayout.createSequentialGroup()
                .addGroup(ButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TDutyButton, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(RoutineButton, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BackHomeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout BackendPanelLayout = new javax.swing.GroupLayout(BackendPanel);
        BackendPanel.setLayout(BackendPanelLayout);
        BackendPanelLayout.setHorizontalGroup(
            BackendPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(BackendPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(BackendPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(HomeScreen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(BackendPanelLayout.createSequentialGroup()
                        .addComponent(Buttons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        BackendPanelLayout.setVerticalGroup(
            BackendPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(BackendPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(HomeScreen, javax.swing.GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE)
                .addGap(44, 44, 44)
                .addComponent(Buttons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(BackendPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(BackendPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void RoutineButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RoutineButtonActionPerformed

        HomeScreen.removeAll();
        HomeScreen.repaint();
        HomeScreen.revalidate();
        RoutineTextArea.setText(null);
        DBConnection Dobj = new DBConnection();
        Connection connect = (Connection) Dobj.dbConnect(DBConnection.dataBaseUrl, DBConnection.dataBaseUserName, DBConnection.dataBasePassWord);
        Statement statement = null;
        ResultSet resultset = null;
        RoutineTextArea.setFont(font);
        String query = "SELECT * FROM exam_roster join course using(course_id)";
        try {
            statement = (Statement) connect.createStatement();
            resultset = statement.executeQuery(query);
            RoutineTextArea.append("Course   Semester  Year   Building    Room No.       Date & Time\n");
            RoutineTextArea.append("------------------------------------------------------------------------\n");
            while (resultset.next()) {
                RoutineTextArea.append(String.format("%s    %s      %s    %-5s     %s        %s\n", resultset.getString(6), resultset.getString(8), resultset.getString(9), resultset.getString(3), resultset.getString(4), resultset.getString(5)));
            }
            statement.close();
            connect.close();
            resultset.close();
        } catch (SQLException e) {
        }
        HomeScreen.add(RoutinePanel);
        HomeScreen.repaint();
        HomeScreen.revalidate();
    }//GEN-LAST:event_RoutineButtonActionPerformed

    private void BackHomeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BackHomeButtonActionPerformed
        // Clears the previous panel live at Home Panel and re-creates the Welcome Panel into it. 
        HomeScreen.removeAll();
        HomeScreen.repaint();
        HomeScreen.revalidate();
        HomeScreen.add(WelcomePanel);
        HomeScreen.repaint();
        HomeScreen.revalidate();
    }//GEN-LAST:event_BackHomeButtonActionPerformed

    private void NewrtBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NewrtBTNActionPerformed
        // Clears the previous panel live at Home Panel and re-creates the Edit Routine into it.
        DBConnection Dobj = new DBConnection();
        Connection connect = null;
        String userName = null;
        String passWord = null;
        JFrame jpFrame = new JFrame("USERNAME");
        userName = JOptionPane.showInputDialog(jpFrame, "Username");
        {
            JPanel panel = new JPanel();
            JLabel label = new JLabel("Enter a password:");
            JPasswordField pass = new JPasswordField(10);
            panel.add(label);
            panel.add(pass);
            String[] options = new String[]{"OK", "Cancel"};
            int option = JOptionPane.showOptionDialog(null, panel, "input",
                    JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, options, options[1]);
            if (option == 0) // pressing OK button
            {
                char[] password = pass.getPassword();
                passWord = new String(password);
            }
        }
        if (userName == null || passWord == null) {

        } else {
            connect = (Connection) Dobj.dbConnect(DBConnection.dataBaseUrl, userName, passWord);
            if (connect != null) {

                HomeScreen.removeAll();
                HomeScreen.repaint();
                HomeScreen.revalidate();
                ResetTheList(connect);
                //CurrentRoutine.add(RoutineList);
                HomeScreen.add(EditRoutine);
                HomeScreen.repaint();
                HomeScreen.revalidate();
            } else {
                JOptionPane.showMessageDialog(jpFrame, "Username or Password is Wrong!");
            }
        }
    }//GEN-LAST:event_NewrtBTNActionPerformed

    private void RoutineListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_RoutineListMouseClicked

        try {
            String stemp = (String) RoutineList.getSelectedValue();
            if (stemp.length() > 0) {
                String stempL[] = stemp.split(" ");
                examIDtoDel = Integer.parseInt(stempL[0]);
            } else {
                examIDtoDel = -1;
            }
        } catch (Exception e) {
            examIDtoDel = -1;
        }

    }//GEN-LAST:event_RoutineListMouseClicked

    private void DropExamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DropExamActionPerformed
        // Drops a exam from the exam_raoster table
        if (examIDtoDel != -1) {
            DropFromTableExam();
        }
        ResetTheList();
    }//GEN-LAST:event_DropExamActionPerformed

    private void AddNewExamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddNewExamActionPerformed
        // Invokes the New Frame for Adding an Exam Routine in the Exam Roster Table
        HomeScreen.removeAll();
        HomeScreen.repaint();
        HomeScreen.revalidate();
        FillCourseCombo();
        Building_textField.setText(null);
        RoomNo_TextField.setText(null);
        Date_TextField.setText(null);
        Time_TextField.setText(null);
        HomeScreen.add(AddNewExamPanel);
        HomeScreen.repaint();
        HomeScreen.revalidate();
    }//GEN-LAST:event_AddNewExamActionPerformed

    private void AddThisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddThisActionPerformed
        String starr[] = jComboBox1.getSelectedItem().toString().split(" ");
        int CID = Integer.parseInt(starr[0]);
        int RoomNum;
        try {
            RoomNum = Integer.parseInt(RoomNo_TextField.getText());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Please Enter a Valid Room Number.");
            return;
        }
        DBConnection Dobj = new DBConnection();
        Connection connect = (Connection) Dobj.dbConnect(DBConnection.dataBaseUrl, DBConnection.dataBaseUserName, DBConnection.dataBasePassWord);
        String dateTime = Date_TextField.getText() + " " + Time_TextField.getText();
        String qr1 = String.format("insert into exam_roster (course_id,building_name,room_no,exam_time) values(%d,'%s',%d,'%s')",
                CID, Building_textField.getText(), Integer.parseInt(RoomNo_TextField.getText()), dateTime);
        System.out.println(qr1);
        try {
            connect.createStatement().executeUpdate(qr1);
            HomeScreen.removeAll();
            HomeScreen.repaint();
            HomeScreen.revalidate();
            ResetTheList(connect);
            //CurrentRoutine.add(RoutineList);
            HomeScreen.add(EditRoutine);
            HomeScreen.repaint();
            HomeScreen.revalidate();
            JOptionPane.showMessageDialog(null, "Successfully Added!");
            connect.close();
        } catch (SQLException E) {
            System.out.println(E);
        }
    }//GEN-LAST:event_AddThisActionPerformed

    private void Building_textFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Building_textFieldActionPerformed

    }//GEN-LAST:event_Building_textFieldActionPerformed

    private void TeacherAssignListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TeacherAssignListMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_TeacherAssignListMouseClicked

    private void TDutyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TDutyButtonActionPerformed
        // Populates the TeacherAssignList from the teacher_assign table:
        HomeScreen.removeAll();
        HomeScreen.repaint();
        HomeScreen.revalidate();
        DBConnection Dobj = new DBConnection();
        Connection connect = (Connection) Dobj.dbConnect(DBConnection.dataBaseUrl, DBConnection.dataBaseUserName, DBConnection.dataBasePassWord);
        ResetTeacherDutyList(connect);
        HomeScreen.add(TDutyPanel);
        HomeScreen.repaint();
        HomeScreen.revalidate();
    }//GEN-LAST:event_TDutyButtonActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed

    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void RoomNo_TextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RoomNo_TextFieldActionPerformed

    }//GEN-LAST:event_RoomNo_TextFieldActionPerformed

    private void Date_TextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Date_TextFieldActionPerformed

    }//GEN-LAST:event_Date_TextFieldActionPerformed

    private void Time_TextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Time_TextFieldActionPerformed

    }//GEN-LAST:event_Time_TextFieldActionPerformed

    private void printBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printBtnActionPerformed
        try {
            boolean done = RoutineTextArea.print();
            if (done) {
                JOptionPane.showMessageDialog(null, "Done Printing!", "Information", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Printing!", "Printer", JOptionPane.ERROR_MESSAGE);
            }
        } catch (PrinterException E) {
            JOptionPane.showMessageDialog(null, E);
        }
    }//GEN-LAST:event_printBtnActionPerformed

    private void printbtn2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printbtn2ActionPerformed
        try {
            JTextArea pf = new JTextArea();
            pf.setFont(font);
            pf.append("TeacherCode Buidling Room            Date & Time\n");
            pf.append("---------------------------------------------------------\n");
            for (String temp1 : temp) {
                pf.append(temp1);
            }
            boolean done = pf.print();
            if (done) {
                JOptionPane.showMessageDialog(null, "Done Printing!", "Information", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Printing!", "Printer", JOptionPane.ERROR_MESSAGE);
            }
        } catch (PrinterException E) {
            JOptionPane.showMessageDialog(null, E);
        }
    }//GEN-LAST:event_printbtn2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new Home().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AddNewExam;
    private javax.swing.JPanel AddNewExamPanel;
    private javax.swing.JButton AddThis;
    private javax.swing.JButton BackHomeButton;
    private javax.swing.JPanel BackendPanel;
    private javax.swing.JLabel BuildingRoomjLabel;
    private javax.swing.JLabel Building_name_to_beinser;
    private javax.swing.JTextField Building_textField;
    private javax.swing.JPanel Buttons;
    private javax.swing.JScrollPane CurrentRoutine;
    private javax.swing.JTextField Date_TextField;
    private javax.swing.JLabel Date_to_beinser;
    private javax.swing.JButton DropExam;
    private javax.swing.JPanel EditRoutine;
    private javax.swing.JPanel HomeScreen;
    private javax.swing.JButton NewrtBTN;
    private javax.swing.JTextField RoomNo_TextField;
    private javax.swing.JLabel RoomNo_to_beinser;
    private javax.swing.JButton RoutineButton;
    private javax.swing.JInternalFrame RoutineInternalFrame;
    private javax.swing.JList RoutineList;
    private javax.swing.JPanel RoutinePanel;
    private javax.swing.JScrollPane RoutineScrollPane;
    private javax.swing.JTextArea RoutineTextArea;
    private javax.swing.JButton TDutyButton;
    private javax.swing.JPanel TDutyPanel;
    private javax.swing.JList TeacherAssignList;
    private javax.swing.JLabel TeacherCodejLabel;
    private javax.swing.JScrollPane TeachersAssign;
    private javax.swing.JLabel TimeOftheExamjLabel;
    private javax.swing.JTextField Time_TextField;
    private javax.swing.JLabel Time_to_beinser;
    private javax.swing.JLabel WelcomeLabel;
    private javax.swing.JPanel WelcomePanel;
    private javax.swing.JLabel course_to_binser;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JButton printBtn;
    private javax.swing.JButton printbtn2;
    // End of variables declaration//GEN-END:variables
}

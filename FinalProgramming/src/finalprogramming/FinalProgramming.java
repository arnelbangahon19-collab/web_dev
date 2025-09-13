package finalprogramming;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class FinalProgramming extends JFrame {

    // Data
    static ArrayList<String> MenuList = new ArrayList<>();
    static ArrayList<Double> MenuPriceList = new ArrayList<>();
    static ArrayList<String> MenuCategoryList = new ArrayList<>();
    static int orderCounter = 1;

    // GUI components
    private JTextField waiterField, tableField, customerField;
    private JRadioButton dineInBtn, takeOutBtn;
    private JList<String> coursesList, itemsList;
    private DefaultListModel<String> coursesModel, itemsModel;
    private JTextArea receiptArea;

    // Menu data
    private String[] menuItems = {
            "Bruschetta - Php 120",
            "Spring Rolls - Php 150",
            "Garlic Bread - Php 200",
            "Cream of Mushroom - Php 200",
            "Chicken Soup - Php 250",
            "Garden Salad - Php 180",
            "Grilled Chicken - Php 350",
            "Steak - Php 600",
            "Chocolate Cake - Php 220",
            "Ice Cream - Php 150"
    };

    private double[] menuPrices = {120, 150, 200, 200, 250, 180, 350, 600, 220, 150};

    private String[] menuCategories = {
            "Appetizer","Appetizer","Appetizer",
            "Soup","Soup",
            "Salad",
            "Main Course","Main Course",
            "Dessert","Dessert"
    };

    public FinalProgramming() {
        setTitle("Order System POS");
        setSize(950,600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ===== TOP LOGO =====
        ImageIcon logoIcon = new ImageIcon("Bon Appétit by Chef Nel.png");
        Image scaledImage = logoIcon.getImage().getScaledInstance(150,200,Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledImage));
        logoLabel.setHorizontalAlignment(JLabel.CENTER);
        add(logoLabel, BorderLayout.NORTH);

        // ===== LEFT PANEL =====
        JPanel leftPanel = new JPanel(new BorderLayout());

        // Form
        JPanel formPanel = new JPanel(new GridLayout(4,2,5,5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Order Details"));

        formPanel.add(new JLabel("Customer Name:"));
        customerField = new JTextField();
        formPanel.add(customerField);

        formPanel.add(new JLabel("Waiter Name:"));
        waiterField = new JTextField();
        formPanel.add(waiterField);

        formPanel.add(new JLabel("Table No:"));
        tableField = new JTextField();
        formPanel.add(tableField);

        dineInBtn = new JRadioButton("Dine In");
        takeOutBtn = new JRadioButton("Take Out");
        ButtonGroup group = new ButtonGroup();
        group.add(dineInBtn); group.add(takeOutBtn);
        dineInBtn.setSelected(true);

        JPanel radioPanel = new JPanel();
        radioPanel.add(dineInBtn);
        radioPanel.add(takeOutBtn);

        formPanel.add(new JLabel("Order Type:"));
        formPanel.add(radioPanel);

        leftPanel.add(formPanel, BorderLayout.NORTH);

        // Courses list
        coursesModel = new DefaultListModel<>();
        String[] categories = {"Appetizer","Soup","Salad","Main Course","Dessert"};
        for(String c: categories) coursesModel.addElement(c);
        coursesList = new JList<>(coursesModel);
        coursesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane courseScroll = new JScrollPane(coursesList);
        courseScroll.setBorder(BorderFactory.createTitledBorder("Courses"));
        leftPanel.add(courseScroll, BorderLayout.WEST);

        // Items list
        itemsModel = new DefaultListModel<>();
        itemsList = new JList<>(itemsModel);
        itemsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane itemsScroll = new JScrollPane(itemsList);
        itemsScroll.setBorder(BorderFactory.createTitledBorder("Items"));
        leftPanel.add(itemsScroll, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1,4,5,5));
        JButton addBtn = new JButton("Add");
        JButton removeBtn = new JButton("Remove");
        JButton payBtn = new JButton("Pay");
        JButton exitBtn = new JButton("Exit");
        buttonPanel.add(addBtn); buttonPanel.add(removeBtn);
        buttonPanel.add(payBtn); buttonPanel.add(exitBtn);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(leftPanel, BorderLayout.CENTER);

        // ===== RIGHT PANEL (Receipt) =====
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Receipt"));

        JLabel receiptLogo = new JLabel(new ImageIcon(scaledImage));
        receiptLogo.setHorizontalAlignment(JLabel.CENTER);
        rightPanel.add(receiptLogo, BorderLayout.NORTH);

        receiptArea = new JTextArea();
        receiptArea.setEditable(false);
        JScrollPane receiptScroll = new JScrollPane(receiptArea);
        rightPanel.add(receiptScroll, BorderLayout.CENTER);

        add(rightPanel, BorderLayout.EAST);

        // ===== ACTIONS =====
        dineInBtn.addActionListener(e -> tableField.setEditable(true));
        takeOutBtn.addActionListener(e -> {
            tableField.setText("");
            tableField.setEditable(false);
        });

        coursesList.addListSelectionListener(e -> {
            if(!e.getValueIsAdjusting()) updateItems(coursesList.getSelectedValue());
        });
        addBtn.addActionListener(e -> addOrder());
        removeBtn.addActionListener(e -> removeOrder());
        payBtn.addActionListener(e -> processPayment());
        exitBtn.addActionListener(e -> System.exit(0));

        setVisible(true);
    }

    private void updateItems(String course){
        itemsModel.clear();
        for(int i=0;i<menuItems.length;i++){
            if(menuCategories[i].equals(course)) itemsModel.addElement(menuItems[i]);
        }
    }

    private void addOrder(){
        if(customerField.getText().trim().isEmpty()){
            JOptionPane.showMessageDialog(this,"Please enter Customer Name first.");
            return;
        }

        int index = itemsList.getSelectedIndex();
        if(index==-1){ JOptionPane.showMessageDialog(this,"Select an item"); return; }
        String itemFull = itemsModel.get(index);
        String qtyStr = JOptionPane.showInputDialog(this,"Enter quantity for "+itemFull+":");
        try{
            int qty = Integer.parseInt(qtyStr);
            if(qty<=0) throw new NumberFormatException();
            String item = itemFull.split("-")[0].trim();
            double price = Double.parseDouble(itemFull.split("Php")[1].trim())*qty;
            MenuList.add(item+" x"+qty);
            MenuPriceList.add(price);
            MenuCategoryList.add(coursesList.getSelectedValue());
            updateReceipt();
        }catch(NumberFormatException ex){
            JOptionPane.showMessageDialog(this,"Invalid quantity.");
        }
    }

    private void removeOrder(){
        if(MenuList.isEmpty()){ JOptionPane.showMessageDialog(this,"No orders to remove."); return; }
        MenuList.remove(MenuList.size()-1);
        MenuPriceList.remove(MenuPriceList.size()-1);
        MenuCategoryList.remove(MenuCategoryList.size()-1);
        updateReceipt();
    }

    private void processPayment(){
        if(MenuList.isEmpty()){ JOptionPane.showMessageDialog(this,"No orders placed."); return; }
        double total = 0;
        for(double p: MenuPriceList) total+=p;
        while(true){
            String paymentStr = JOptionPane.showInputDialog(this,"Total: Php "+total+"\nEnter Payment:");
            if(paymentStr==null) return;
            try{
                double payment = Double.parseDouble(paymentStr);
                if(payment<total) JOptionPane.showMessageDialog(this,"Insufficient funds! Try again.");
                else{
                    double change = payment-total;
                    JOptionPane.showMessageDialog(this,"Payment successful!\nChange: Php "+change);
                    saveReport(total,payment,change);
                    MenuList.clear(); MenuPriceList.clear(); MenuCategoryList.clear();
                    updateReceipt();
                    break;
                }
            }catch(NumberFormatException ex){ JOptionPane.showMessageDialog(this,"Invalid input."); }
        }
    }

    private void updateReceipt(){
        receiptArea.setText("");
        receiptArea.append("===== Bon Appétit by Chef Nel =====\n");
        receiptArea.append("Customer: "+customerField.getText()+"\n");
        receiptArea.append("Waiter: "+waiterField.getText()+"\n");
        if(dineInBtn.isSelected()){
            receiptArea.append("Table: "+tableField.getText()+"\n");
        }
        receiptArea.append("Type: "+(dineInBtn.isSelected()?"Dine In":"Take Out")+"\n");
        receiptArea.append("-----------------------------\n");

        double total=0;
        for(int i=0;i<MenuList.size();i++){
            receiptArea.append(MenuCategoryList.get(i)+": "+MenuList.get(i)+" - Php "+MenuPriceList.get(i)+"\n");
            total+=MenuPriceList.get(i);
        }
        receiptArea.append("-----------------------------\n");
        receiptArea.append("TOTAL: Php "+total+"\n");
    }

    private void saveReport(double total,double payment,double change){
        try{
            String filename = "orders-"+LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))+".csv";
            boolean newFile = !(new File(filename).exists());
            PrintWriter out = new PrintWriter(new FileWriter(filename,true));
            if(newFile) out.println("OrderNo,DateTime,Customer,Waiter,Table,Type,Item,Price,Total,Payment,Change,Category");
            String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            for(int i=0;i<MenuList.size();i++){
                out.printf("%d,%s,%s,%s,%s,%s,%s,%.2f,%.2f,%.2f,%.2f,%s%n",
                        orderCounter,dateTime,
                        customerField.getText(),
                        waiterField.getText(),
                        (dineInBtn.isSelected()?tableField.getText():""),
                        (dineInBtn.isSelected()?"Dine In":"Take Out"),
                        MenuList.get(i),MenuPriceList.get(i),
                        total,payment,change,
                        MenuCategoryList.get(i));
            }
            out.close(); orderCounter++;
        }catch(IOException e){ e.printStackTrace(); }
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(FinalProgramming::new);
    }
}

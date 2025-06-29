import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

// Contact class to represent a single contact
class Contact {
    private String name;
    private String phoneNumber;
    private String email;

    public Contact(String name, String phoneNumber, String email) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Name: " + name + ", Phone: " + phoneNumber + ", Email: " + email;
    }
}

// FileHandler class to handle file operations
class FileHandler {
    private static final String FILE_NAME = "contacts.dat";

    public static void saveContacts(List<Contact> contacts) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(contacts);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Contact> loadContacts() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (List<Contact>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}

// ContactManager class to manage contacts
class ContactManager {
    private List<Contact> contacts;

    public ContactManager() {
        this.contacts = FileHandler.loadContacts();
    }

    public void addContact(Contact contact) {
        contacts.add(contact);
        FileHandler.saveContacts(contacts);
    }

    public void updateContact(int index, Contact newContact) {
        if (index >= 0 && index < contacts.size()) {
            contacts.set(index, newContact);
            FileHandler.saveContacts(contacts);
        }
    }

    public void deleteContact(int index) {
        if (index >= 0 && index < contacts.size()) {
            contacts.remove(index);
            FileHandler.saveContacts(contacts);
        }
    }

    public List<Contact> getAllContacts() {
        return new ArrayList<>(contacts);
    }

    public Contact getContact(int index) {
        if (index >= 0 && index < contacts.size()) {
            return contacts.get(index);
        }
        return null;
    }
}

// Main GUI class
public class ContactManagerGUI extends JFrame {
    private ContactManager contactManager;
    private DefaultListModel<String> listModel;
    private JList<String> contactsList;

    public ContactManagerGUI() {
        contactManager = new ContactManager();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Contact Manager");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create components
        listModel = new DefaultListModel<>();
        contactsList = new JList<>(listModel);
        updateContactsList();

        JButton addButton = new JButton("Add Contact");
        JButton editButton = new JButton("Edit Contact");
        JButton deleteButton = new JButton("Delete Contact");
        JButton viewButton = new JButton("View Contact");

        // Set layout
        setLayout(new BorderLayout());

        // Add components to the frame
        add(new JScrollPane(contactsList), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(viewButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add action listeners
        addButton.addActionListener(e -> addContact());
        editButton.addActionListener(e -> editContact());
        deleteButton.addActionListener(e -> deleteContact());
        viewButton.addActionListener(e -> viewContact());
    }

    private void updateContactsList() {
        listModel.clear();
        for (Contact contact : contactManager.getAllContacts()) {
            listModel.addElement(contact.getName() + " - " + contact.getPhoneNumber());
        }
    }

    private void addContact() {
        JTextField nameField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField emailField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Phone Number:"));
        panel.add(phoneField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);

        int result = JOptionPane.showConfirmDialog(
                this, panel, "Add New Contact",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();

            if (!name.isEmpty() && !phone.isEmpty()) {
                Contact contact = new Contact(name, phone, email);
                contactManager.addContact(contact);
                updateContactsList();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Name and Phone Number are required!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editContact() {
        int selectedIndex = contactsList.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a contact to edit.", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Contact contact = contactManager.getContact(selectedIndex);
        if (contact == null) return;

        JTextField nameField = new JTextField(contact.getName());
        JTextField phoneField = new JTextField(contact.getPhoneNumber());
        JTextField emailField = new JTextField(contact.getEmail());

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Phone Number:"));
        panel.add(phoneField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);

        int result = JOptionPane.showConfirmDialog(
                this, panel, "Edit Contact",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();

            if (!name.isEmpty() && !phone.isEmpty()) {
                Contact updatedContact = new Contact(name, phone, email);
                contactManager.updateContact(selectedIndex, updatedContact);
                updateContactsList();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Name and Phone Number are required!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteContact() {
        int selectedIndex = contactsList.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a contact to delete.", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this, 
                "Are you sure you want to delete this contact?", 
                "Confirm Deletion", 
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            contactManager.deleteContact(selectedIndex);
            updateContactsList();
        }
    }

    private void viewContact() {
        int selectedIndex = contactsList.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a contact to view.", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Contact contact = contactManager.getContact(selectedIndex);
        if (contact != null) {
            JOptionPane.showMessageDialog(this, 
                contact.toString(), 
                "Contact Details", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ContactManagerGUI gui = new ContactManagerGUI();
            gui.setVisible(true);
        });
    }
}
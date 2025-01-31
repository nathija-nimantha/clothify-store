package org.clothifyStore.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.clothifyStore.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class Customer {
    @Id
    private String id;
    private String name;
    private String address;
    private LocalDate dob;
    private String phone;
    private String gender;
    private String email;
    private String password;
    private boolean enableNewsLetter;

    public boolean saveCustomer(org.clothifyStore.dto.Customer customer) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        String query = "INSERT INTO customer (id, name, address, dob, phoneNumber, gender, email, password, newsletter) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, customer.getId());
        preparedStatement.setString(2, customer.getName());
        preparedStatement.setString(3, customer.getAddress());
        preparedStatement.setString(4, customer.getDob().toString());
        preparedStatement.setString(5, customer.getPhoneNumber());
        preparedStatement.setString(6, customer.getGender());
        preparedStatement.setString(7, customer.getEmail());
        preparedStatement.setString(8, customer.getPassword());
        preparedStatement.setBoolean(9, enableNewsLetter);
        return preparedStatement.executeUpdate() > 0;
    }
}

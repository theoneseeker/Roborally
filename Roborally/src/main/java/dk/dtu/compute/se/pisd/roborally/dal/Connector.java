/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.dal;

import com.mysql.cj.util.StringUtils;
import dk.dtu.compute.se.pisd.roborally.fileaccess.IOUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
class Connector {

    private static final String HOST     = "localhost";
    private static final int    PORT     = 3306;
    private static final String DATABASE = "didofjg";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "kusser";

    private static final String DELIMITER = ";;";

    private Connection connection;

    /**
     * Used to establish a connection to a database.
     */
    Connector() {
        try {
            // String url = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE;
            String url = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE + "?serverTimezone=UTC";
            connection = DriverManager.getConnection(url, USERNAME, PASSWORD);

            createDatabaseSchema();
        } catch (SQLException e) {
            System.out.print("Could not establish connection to the database");
            e.printStackTrace();
            // Platform.exit();
        }
    }

    /**
     * Creates tables in the database according to the sql file.
     */
    private void createDatabaseSchema() {

        String createTablesStatement =
                IOUtil.readResource("schemas/createschema.sql");

        try {
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();
            for (String sql : createTablesStatement.split(DELIMITER)) {
                if (!StringUtils.isEmptyOrWhitespaceOnly(sql)) {
                    statement.executeUpdate(sql);
                }
            }

            statement.close();
            connection.commit();
        } catch (SQLException e) {
            System.out.println("Could not create schemas in database");
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e1) {
                System.out.println("Failed to rollback after failing to create schemas in database.");
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.print("Could not set auto commit to true - possible connection error.");
            }
        }
    }

    Connection getConnection() {
        return connection;
    }

}

package com.mycompany.reseep;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Development/testing utility: visiting this URL wipes every row from the
 * three application tables, in an order that respects the foreign keys
 * (recipe_ingredient depends on both recipe and ingredient, so it goes
 * first). Not something you'd expose in a real deployment — see the
 * README's "Limitations" section.
 *
 * Visit: http://localhost:8080/reseep/reset
 */
@WebServlet("/reset")
public class ResetServlet extends HttpServlet {

    @Resource(lookup = "jdbc/ReSeePPool")
    private DataSource dataSource;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain");
        PrintWriter out = resp.getWriter();

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            int recipeIngredientRows = stmt.executeUpdate("DELETE FROM recipe_ingredient");
            int recipeRows = stmt.executeUpdate("DELETE FROM recipe");
            int ingredientRows = stmt.executeUpdate("DELETE FROM ingredient");

            out.println("Tables cleared.");
            out.println("recipe_ingredient rows deleted: " + recipeIngredientRows);
            out.println("recipe rows deleted: " + recipeRows);
            out.println("ingredient rows deleted: " + ingredientRows);

        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("Failed to clear tables: " + e.getMessage());
        }
    }
}
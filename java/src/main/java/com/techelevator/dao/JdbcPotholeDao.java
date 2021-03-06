package com.techelevator.dao;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.techelevator.model.Pothole;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@Component
public class JdbcPotholeDao  implements PotholeDao{
    private JdbcTemplate jdbcTemplate;

    public JdbcPotholeDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Pothole> getAllOpenPotholes() {
        List<Pothole> potholeList = new ArrayList<>();

        String sql = "SELECT pothole_id, date_reported, latitude, longitude, image_location, cross_street_1, cross_street_2, contact_name, contact_email, contact_phone, " +
                "pending, severity, repair_status, repair_date, inspected " +
                "FROM pothole " +
                "WHERE pending = false AND repair_status != 'completed';";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);

        while(results.next()) {
            Pothole pothole = mapRowToPothole(results);
            potholeList.add(pothole);
        }
        return potholeList;
    }

    @Override
    public void addPothole(Pothole pothole) {
        String sql = "INSERT INTO pothole (date_reported, latitude, longitude, image_location, cross_street_1, cross_street_2, contact_name, contact_email, contact_phone, inspected, pending, severity, repair_status) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, false, true, ?, 'unscheduled')";
        jdbcTemplate.update(sql, pothole.getDateReported(), pothole.getLatitude(), pothole.getLongitude(), pothole.getImageUrl(), pothole.getCrossStreet1(), pothole.getCrossStreet2(),pothole.getContactName(), pothole.getContactEmail(), pothole.getContactPhone(), pothole.getSeverity());

    }

    @Override
    public void deletePothole(int potholeId) {
        String sql = "DELETE FROM pothole WHERE pothole_id = ?";
        jdbcTemplate.update(sql, potholeId);
    }

    @Override
    public void updatePotholeStatus(Pothole pothole) {
        String sql = "UPDATE pothole SET pending = false WHERE pothole_id = ?";
        jdbcTemplate.update(sql,  pothole.getPotholeId());
    }

    @Override
    public List<Pothole> getPendingPotholes() {
        List<Pothole> pendingPotholes = new ArrayList<>();
        String sql = "SELECT pothole_id, date_reported, latitude, longitude, image_location, cross_street_1, cross_street_2, contact_name, contact_email, contact_phone, " +
                "pending, severity, repair_status, repair_date, inspected " +
                "FROM pothole " +
                "WHERE pothole.pending = TRUE";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while(results.next()) {
            Pothole pothole = mapRowToPothole(results);
            pendingPotholes.add(pothole);
        }
        return pendingPotholes;
    }

    @Override
    public List<Pothole> getAllPotholes() {
        List<Pothole> potholeList = new ArrayList<>();

        String sql = "SELECT pothole_id, date_reported, latitude, longitude, image_location, cross_street_1, cross_street_2, contact_name, contact_email, contact_phone, " +
                "pending, severity, repair_status, repair_date, inspected " +
                "FROM pothole";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);

        while(results.next()) {
            Pothole pothole = mapRowToPothole(results);
            potholeList.add(pothole);
        }
        return potholeList;


    }

    @Override
    public List<Pothole> getRepairedPotholes() {
        List<Pothole> pendingPotholes = new ArrayList<>();
        String sql = "SELECT pothole_id, date_reported, latitude, longitude, image_location, cross_street_1, cross_street_2, contact_name, contact_email, contact_phone, " +
                "pending, severity, repair_status, repair_date, inspected " +
                "FROM pothole " +
                "WHERE pothole.repair_status = 'completed'";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while(results.next()) {
            Pothole pothole = mapRowToPothole(results);
            pendingPotholes.add(pothole);
        }
        return pendingPotholes;
    }

    @Override
    public List<Pothole> getScheduledPotholes() {
        List<Pothole> pendingPotholes = new ArrayList<>();
        String sql = "SELECT pothole_id, date_reported, latitude, longitude, image_location, cross_street_1, cross_street_2, contact_name, contact_email, contact_phone, " +
                "pending, severity, repair_status, repair_date, inspected " +
                "FROM pothole " +
                "WHERE pothole.repair_status = 'scheduled'";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while(results.next()) {
            Pothole pothole = mapRowToPothole(results);
            pendingPotholes.add(pothole);
        }
        return pendingPotholes;
    }

    @Override
    public List<Pothole> getUnscheduledPotholes() {
        List<Pothole> pendingPotholes = new ArrayList<>();
        String sql = "SELECT pothole_id, date_reported, latitude, longitude, image_location, cross_street_1, cross_street_2, contact_name, contact_email, contact_phone, " +
                "pending, severity, repair_status, repair_date, inspected " +
                "FROM pothole " +
                "WHERE pothole.repair_status = 'unscheduled' AND pothole.pending = false";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while(results.next()) {
            Pothole pothole = mapRowToPothole(results);
            pendingPotholes.add(pothole);
        }
        return pendingPotholes;
    }

    @Override
    public void updatePotholeRepairStatus(Pothole pothole) {
        String sql = "UPDATE pothole SET repair_status = ? WHERE pothole_id = ?";
        jdbcTemplate.update(sql, pothole.getRepairStatus(), pothole.getPotholeId());
    }

    @Override
    public void repairPothole(Pothole pothole) {
        LocalDate today = LocalDate.now();
        String sql = "UPDATE pothole SET repair_date = ?, repair_status = 'completed' WHERE pothole_id = ?";
        jdbcTemplate.update(sql, today, pothole.getPotholeId());
    }

    @Override
    public Integer countRepaired() {
        int repaired = 0;
        String sql = "SELECT count(*) FROM pothole WHERE repair_status = 'completed'";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        if (results.next()) {
            repaired = results.getInt("count");
        }
        return repaired;
    }

    @Override
    public Integer countUnscheduled() {
        int unscheduled = 0;
        String sql = "SELECT count(*) FROM pothole WHERE repair_status = 'unscheduled' AND pending = 'false'";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        if (results.next()) {
            unscheduled = results.getInt("count");
        }
        return unscheduled;
    }

    @Override
    public Integer countScheduled() {
        int scheduled = 0;
        String sql = "SELECT count(*) FROM pothole WHERE repair_status = 'scheduled'";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        if (results.next()) {
            scheduled = results.getInt("count");
        }
        return scheduled;
    }

    @Override
    public List<Pothole> getAllVerifiedPotholes() {
        List<Pothole> potholeList = new ArrayList<>();

        String sql = "SELECT pothole_id, date_reported, latitude, longitude, image_location, cross_street_1, cross_street_2, contact_name, contact_email, contact_phone, " +
                "pending, severity, repair_status, repair_date, inspected " +
                "FROM pothole " +
                "WHERE pending = false;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);

        while(results.next()) {
            Pothole pothole = mapRowToPothole(results);
            potholeList.add(pothole);
        }
        return potholeList;
    }

    @Override
    public void showDetails(Pothole pothole) {
        String sql = "UPDATE pothole SET showDetails = ? WHERE pothole_id = ?";
        jdbcTemplate.update(sql, pothole.isShowStatus(), pothole.getPotholeId());
    }

    @Override
    public List<Pothole> getAllReportedPotholes() {
        List<Pothole> potholeList = new ArrayList<>();

        String sql = "SELECT pothole_id, date_reported, latitude, longitude, image_location, cross_street_1, cross_street_2, contact_name, contact_email, contact_phone, " +
                "pending, severity, repair_status, repair_date, inspected " +
                "FROM pothole " +
                "WHERE repair_status != 'completed';";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);

        while(results.next()) {
            Pothole pothole = mapRowToPothole(results);
            potholeList.add(pothole);
        }
        return potholeList;
    }

    @Override
    public void updatePotholeRepairDate(Pothole pothole) {
        String sql = "UPDATE pothole SET repair_date = ?, employee_id = ? WHERE pothole_id = ?";
        jdbcTemplate.update(sql, pothole.getRepairDate(), pothole.getEmployeeId(), pothole.getPotholeId());
    }

    @Override
    public List<Pothole> getPotholesByEmployeeId(int employeeId) {
        List<Pothole> potholes = new ArrayList<>();
        String sql = "SELECT * FROM pothole JOIN employee ON pothole.employee_id = employee.employee_id WHERE employee.employee_id = ? AND repair_status = 'scheduled' ORDER BY repair_date ASC;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, employeeId);
        while (results.next()) {
            Pothole pothole = mapRowToPothole(results);
            pothole.setEmployeeFirstName(results.getString("first_name"));
            pothole.setEmployeeLastName(results.getString("last_name"));
            pothole.setEmployeeId(results.getInt("employee_id"));
            potholes.add(pothole);

        }
        return potholes;
    }

    @Override
    public List<Pothole> getScheduledPotholesWithEmployeeInfo() {
        List<Pothole> potholes = new ArrayList<>();
        String sql = "SELECT * FROM pothole JOIN employee ON pothole.employee_id = employee.employee_id WHERE repair_status = 'scheduled' ORDER BY repair_date ASC;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()) {
            Pothole pothole = mapRowToPothole(results);
            pothole.setEmployeeFirstName(results.getString("first_name"));
            pothole.setEmployeeLastName(results.getString("last_name"));
            pothole.setEmployeeId(results.getInt("employee_id"));
            potholes.add(pothole);

        }
        return potholes;
    }

    private Pothole mapRowToPothole(SqlRowSet results) {
        Pothole pothole = new Pothole();
        pothole.setPotholeId(results.getInt("pothole_id"));
        pothole.setDateReported(LocalDate.parse(results.getString("date_reported")));
        pothole.setLatitude(results.getBigDecimal("latitude").setScale(6));
        pothole.setLongitude(results.getBigDecimal("longitude").setScale(6));
        pothole.setImageUrl(results.getString("image_location"));
        pothole.setCrossStreet1(results.getString("cross_street_1"));
        pothole.setCrossStreet2(results.getString("cross_street_2"));
        pothole.setContactName(results.getString("contact_name"));
        pothole.setContactEmail(results.getString("contact_email"));
        pothole.setContactPhone(results.getString("contact_phone"));
        pothole.setPending(results.getBoolean("pending"));
        pothole.setSeverity(results.getString("severity"));
        pothole.setInspected(results.getBoolean("inspected"));
        pothole.setRepairStatus(results.getString("repair_status"));
        if (results.getString("repair_date") != null) {
            pothole.setRepairDate(LocalDate.parse(results.getString("repair_date")));
        }
        return pothole;
    }
}

package repository.implementation.mapper;

import model.Advisor;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AdvisorResultSetMapper {

    public Advisor map(ResultSet rs) throws SQLException {
        Advisor advisor = new Advisor();
        advisor.setId(rs.getInt("id"));
        advisor.setFirstName(rs.getString("first_name"));
        advisor.setLastName(rs.getString("last_name"));
        advisor.setEmail(rs.getString("email"));
        advisor.setSpeciality(rs.getString("speciality"));
        advisor.setWorkloadScore(rs.getDouble("workload_score"));
        return advisor;
    }
}

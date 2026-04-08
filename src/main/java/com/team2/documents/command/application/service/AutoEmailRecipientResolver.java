package com.team2.documents.command.application.service;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class AutoEmailRecipientResolver {

    private final JdbcTemplate jdbcTemplate;

    public AutoEmailRecipientResolver(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<String> findBuyerEmailsByClientId(Integer clientId) {
        return jdbcTemplate.queryForList(
                """
                SELECT DISTINCT buyer_email
                FROM team2_master.buyers
                WHERE client_id = ?
                  AND buyer_email IS NOT NULL
                  AND buyer_email <> ''
                ORDER BY buyer_id
                """,
                String.class,
                clientId
        );
    }

    public String findPrimaryBuyerNameByClientId(Integer clientId) {
        List<String> names = jdbcTemplate.queryForList(
                """
                SELECT buyer_name
                FROM team2_master.buyers
                WHERE client_id = ?
                  AND buyer_name IS NOT NULL
                  AND buyer_name <> ''
                ORDER BY buyer_id
                LIMIT 1
                """,
                String.class,
                clientId
        );
        return names.isEmpty() ? "Buyer" : names.get(0);
    }

    public List<String> findProductionTeamEmails() {
        return findTeamEmailsByRole("production");
    }

    public List<String> findShippingTeamEmails() {
        return findTeamEmailsByRole("shipping");
    }

    private List<String> findTeamEmailsByRole(String role) {
        return jdbcTemplate.queryForList(
                """
                SELECT DISTINCT user_email
                FROM team2_auth.users
                WHERE user_role = ?
                  AND user_email IS NOT NULL
                  AND user_email <> ''
                  AND LOWER(COALESCE(user_status, '')) NOT IN ('retired', 'on_leave')
                  AND COALESCE(user_status, '') NOT IN ('퇴직', '휴직')
                ORDER BY user_id
                """,
                String.class,
                role
        );
    }
}

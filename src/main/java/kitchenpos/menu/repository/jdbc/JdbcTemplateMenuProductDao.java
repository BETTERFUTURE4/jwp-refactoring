package kitchenpos.menu.repository.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.sql.DataSource;
import kitchenpos.menu.domain.MenuProduct;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcTemplateMenuProductDao {
    private static final String TABLE_NAME = "menu_product";
    private static final String KEY_COLUMN_NAME = "seq";

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    public JdbcTemplateMenuProductDao(final DataSource dataSource) {
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName(TABLE_NAME)
                .usingGeneratedKeyColumns(KEY_COLUMN_NAME)
        ;
    }

    public MenuProduct save(final MenuProduct entity) {
        if (Objects.isNull(entity.getSeq())) {
            final SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue("seq", entity.getSeq())
                    .addValue("menu_id", entity.getMenuId())
                    .addValue("product_id", entity.getProductId())
                    .addValue("quantity", entity.getQuantity());
            final Number key = jdbcInsert.executeAndReturnKey(parameters);
            return select(key.longValue());
        }
        update(entity);
        return entity;
    }

    public Optional<MenuProduct> findById(final Long id) {
        try {
            return Optional.of(select(id));
        } catch (final EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<MenuProduct> findAll() {
        final String sql = "SELECT seq, menu_id, product_id, quantity FROM menu_product";
        return jdbcTemplate.query(sql, (resultSet, rowNumber) -> toEntity(resultSet));
    }

    public List<MenuProduct> findAllByMenuId(final Long menuId) {
        final String sql = "SELECT seq, menu_id, product_id, quantity FROM menu_product WHERE menu_id = (:menuId)";
        final SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("menuId", menuId);
        return jdbcTemplate.query(sql, parameters, (resultSet, rowNumber) -> toEntity(resultSet));
    }

    private MenuProduct select(final Long id) {
        final String sql = "SELECT seq, menu_id, product_id, quantity FROM menu_product WHERE seq = (:seq)";
        final SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("seq", id);
        return jdbcTemplate.queryForObject(sql, parameters, (resultSet, rowNumber) -> toEntity(resultSet));
    }

    private void update(MenuProduct entity) {
        final String sql = "UPDATE menu_product SET menu_id = (:menu_id)," +
                " product_id = (:product_id), quantity = (:quantity) WHERE seq = (:seq)";
        final SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("menu_id", entity.getMenuId())
                .addValue("product_id", entity.getProductId())
                .addValue("quantity", entity.getQuantity())
                .addValue("seq", entity.getSeq());
        jdbcTemplate.update(sql, parameters);
    }

    private MenuProduct toEntity(final ResultSet resultSet) throws SQLException {
        return new MenuProduct(resultSet.getLong(KEY_COLUMN_NAME),
                resultSet.getLong("menu_id"),
                resultSet.getLong("product_id"),
                resultSet.getLong("quantity"),
                null);
    }
}
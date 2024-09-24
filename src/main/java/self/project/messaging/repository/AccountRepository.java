package self.project.messaging.repository;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import self.project.messaging.model.Account;
import self.project.messaging.model.tables.Accounts;

@Repository
@RequiredArgsConstructor
public class AccountRepository {

    private final DSLContext dsl;

    public Account save(Account account) {
        return dsl.insertInto(Accounts.ACCOUNTS)
                .columns(Accounts.ACCOUNTS.USERNAME,
                        Accounts.ACCOUNTS.PASSWORD,
                        Accounts.ACCOUNTS.PHONE_NUMBER)
                .values(account.getUsername(),
                        account.getPassword(),
                        account.getPhoneNumber())
                .returning(Accounts.ACCOUNTS.ID)
                .fetchOptionalInto(Account.class)
                .orElseThrow(() -> new IllegalArgumentException("Error saving entity: " + account.getId()));
    }

    public Long betterSave(Account account) {
        return dsl.insertInto(Accounts.ACCOUNTS)
                .set(dsl.newRecord(Accounts.ACCOUNTS, account))
                .returning(Accounts.ACCOUNTS.ID)
                .fetchOptionalInto(Account.class)
                .orElseThrow(() -> new IllegalArgumentException("Error saving entity: " + account.getId()))
                .getId();
    }

    public Account update(Account account) {
        return dsl.update(Accounts.ACCOUNTS)
                .set(dsl.newRecord(Accounts.ACCOUNTS, account))
                .where(Accounts.ACCOUNTS.ID.eq(account.getId()))
                .returning()
                .fetchOptionalInto(Account.class)
                .orElseThrow(() -> new IllegalArgumentException("Error updating entity: " + account.getId()));
    }

    public Account findById(Long id) {
        return dsl.selectFrom(Accounts.ACCOUNTS)
                .where(Accounts.ACCOUNTS.ID.eq(id))
                .fetchOneInto(Account.class);
    }

    public Boolean delete(Long id) {
        return dsl.delete(Accounts.ACCOUNTS)
                .where(Accounts.ACCOUNTS.ID.eq(id))
                .execute() == 1;
    }

    public Account testFindById(Long id) {
        return dsl.fetchOptional(Accounts.ACCOUNTS, Accounts.ACCOUNTS.ID.eq(id))
                .orElseThrow(() -> new IllegalArgumentException("Error finding entity: " + id))
                .into(Account.class);
    }

    /*
    One-to-many select example:

    public Country find(Long id) {
        return dsl.selectFrom(Countries.COUNTRIES)
                .where(Countries.COUNTRIES.ID.eq(id))
                .fetchAny()
                .map(r -> {
                    Country country = r.into(Country.class);
                    country.setCities(cityRepository.findAll(Cities.CITIES.COUNTRY_ID.eq(country.getId())));
                    return country;
                });
    }

    Можно вынести маппинг в отдельный класс:

    @Component
    public class CountryRecordMapper implements RecordMapper<CountriesRecord, Country> {

        private final CityRepository cityRepository;

        @Override
        public Country map(CountriesRecord record) {
            Country country = record.into(Country.class);
            country.setCities(cityRepository.findAll(Cities.CITIES.COUNTRY_ID.eq(country.getId())));
            return country;
        }
    }

    Тогда метод в репозитории превратится в:
    public Country findWithCustomMapper(Long id) {
        return dsl.selectFrom(Countries.COUNTRIES)
                .where(Countries.COUNTRIES.ID.eq(id))
                .fetchAny()
                .map(r -> countryRecordMapper.map((CountriesRecord) r));
    }
     */
}

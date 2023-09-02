package com.thesis.business.musicinstrument.account;

import com.thesis.business.musicinstrument.MusicInstrumentException;
import com.thesis.business.musicinstrument.auth.AuthService;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

@RequestScoped
public class AccountService {

    @Inject
    AccountRepository accountRepository;

    @Inject
    AuthService authService;

    @Transactional
    public AccountDTO register(Account account) {

        String hashedPassword = BCrypt.hashpw(account.getPassword(), BCrypt.gensalt());
        account.setPassword(hashedPassword);
        account.setRole("member");

        this.validateExistedUsername(account);

        accountRepository.persist(account);
        String token = authService.generateJWTToken(account.getUsername(), account.getRole());
        return this.convertToDTO(account, token);

    }

    public AccountDTO login(Account account) {

        Account accountInDB = accountRepository.find("username", account.getUsername()).firstResult();

        if (accountInDB != null && BCrypt.checkpw(account.getPassword(), accountInDB.getPassword())){
            String token = authService.generateJWTToken(accountInDB.getUsername(), accountInDB.getRole());
            return this.convertToDTO(accountInDB, token);
        }
        else
            throw new MusicInstrumentException(Response.Status.UNAUTHORIZED, "Sai tên đăng nhập hoặc mật khẩu");

    }

    public List<Account> findAll(){
        return accountRepository.listAll();
    }

    public Account findById(Long id, String username, String role) {

        Account accountInDB;

        if(role.equals("admin"))
            accountInDB = accountRepository.find("id = ?1", id).firstResult();
        else
            accountInDB = accountRepository.find("id = ?1 and username = ?2", id, username).firstResult();
        if (accountInDB == null)
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Account does not exist");
        
        return accountInDB;
    }

    @Transactional
    public void updateById(Long id, Account account, String username, String role){

        Account accountInDB;

        if(role.equals("admin")){
            accountInDB = accountRepository.find("id = ?1", id).firstResult();
            if(accountInDB == null)
                throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Account does not exist");
            accountInDB.setRole(account.getRole());
        }  
        else{
            accountInDB = accountRepository.find("id = ?1 and username = ?2", id, username).firstResult();
            if(accountInDB == null)
                throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Account does not exist");
            accountInDB.setAddress(account.getAddress());
            accountInDB.setPhone(account.getPhone());
        }

        accountRepository.persist(accountInDB);
    }

    @Transactional
    public void deleteById(Long id){
        if(accountRepository.deleteById(id))
            return;
        else
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Account does not exist");
    }

    private void validateExistedUsername(Account account) {

        Account accountInDB = accountRepository.find("username", account.getUsername()).firstResult();
        if (accountInDB != null)
            throw new MusicInstrumentException(Response.Status.BAD_REQUEST, "This username has been used");
    }

    private AccountDTO convertToDTO(Account account, String token){
        
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setId(account.getId());
        accountDTO.setUsername(account.getUsername());
        accountDTO.setAddress(account.getAddress());
        accountDTO.setPhone(account.getPhone());
        accountDTO.setRole(account.getRole());
        accountDTO.setToken(token);
        return accountDTO;
    }

}

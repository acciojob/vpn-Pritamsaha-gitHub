package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ConnectionRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConnectionServiceImpl implements ConnectionService {
    @Autowired
    UserRepository userRepository2;
    @Autowired
    ServiceProviderRepository serviceProviderRepository2;
    @Autowired
    ConnectionRepository connectionRepository2;

    @Override
    public User connect(int userId, String countryName) throws Exception{
        countryName=countryName.toUpperCase();
        User user=userRepository2.findById(userId).get();
        if (user.getConnected()==true){
            throw new Exception("Already connected");
        }else if (user.getCountry().getCountryName().equals(countryName)){
            return user;
        }else{
            if (user.getServiceProviderList()==null){
                throw new Exception("Unable to connect");
            }

            List<ServiceProvider>serviceProviderList=user.getServiceProviderList();

            //Getting smallest serviceprovider ID
            int minimum=Integer.MAX_VALUE;
            ServiceProvider serviceProvider=null;
            Country country=null;

            for (ServiceProvider s:serviceProviderList){
                List<Country>countryList=s.getCountryList();

                for(Country c:countryList){
                    if (c.getCountryName().equals(countryName)  && s.getId()<minimum){
                        minimum=s.getId();
                        serviceProvider=s;
                        country=c;
                    }
                }

            }
            //make a new connection service provider
            if (serviceProvider !=null){
                Connection connection=new Connection();
                connection.setUser(user);
                connection.setServiceProvider(serviceProvider);

                String countryCode=country.getCode();
                int providrId=serviceProvider.getId();
                String makingId=countryCode+"."+providrId+"."+userId;

                //user setup
                user.setMaskIp(makingId);
                user.setConnected(true);
                List<Connection>connectionList=user.getConnectionList();
                connectionList.add(connection);
                user.setConnectionList(connectionList);

                //update serviceprovider connectiuonList
                List<Connection>connectionList1=serviceProvider.getConnectionList();
                connectionList1.add(connection);
                serviceProvider.setConnectionList(connectionList1);

                //only save user
                userRepository2.save(user);
                return user;
            }else{
                throw new Exception("Unable to connect");
            }
        }
    }
    @Override
    public User disconnect(int userId) throws Exception {
        User user=userRepository2.findById(userId).get();
        if (user.getConnected()==false){
            throw new Exception("Already disconnected");
        }

        user.setMaskIp(null);
        user.setConnected(false);
        userRepository2.save(user);
        return user;
    }
    @Override
    public User communicate(int senderId, int receiverId) throws Exception {
        User sender=userRepository2.findById(senderId).get();
        User receiver=userRepository2.findById(receiverId).get();

        String receiverMask=receiver.getMaskIp();

        if (receiverMask !=null){//means receiver already connected a vpn

            String code=receiverMask.substring(0,3);

            if(sender.getCountry().getCode().equals(code)){
                return sender;
            }else{
                //connection not matched, sender need to connect vpn
                String newCountry="";

                if (code.equals(CountryName.IND.toCode())){
                    newCountry=CountryName.IND.toString();
                }
                if (code.equals(CountryName.AUS.toCode())){
                    newCountry=CountryName.AUS.toString();
                }
                if (code.equals(CountryName.USA.toCode())){
                    newCountry=CountryName.USA.toString();
                }
                if (code.equals(CountryName.CHI.toCode())){
                    newCountry=CountryName.CHI.toString();
                }
                if (code.equals(CountryName.JPN.toCode())){
                    newCountry=CountryName.JPN.toString();
                }
                User newUser=connect(senderId,newCountry);
                if (newUser.getConnected()==false){
                    throw new Exception("Cannot establish communication");
                }else{
                    return newUser;
                }
            }
        }else{
            if (receiver.getCountry().equals(sender.getCountry())){//sender and receiver country matched
                return sender;
            }else{
                String receivercountry=receiver.getCountry().getCountryName().toString();
                User updatesender=connect(senderId,receivercountry);

                if (updatesender.getConnected()==false){
                    throw new Exception("Cannot establish communication");
                }else{
                    return updatesender;
                }
            }
        }
    }
}

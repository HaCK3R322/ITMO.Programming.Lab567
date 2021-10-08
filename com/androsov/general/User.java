package com.androsov.general;

import java.io.Serializable;
import java.net.SocketAddress;
import java.util.Objects;

/**
 * The user class represents (don't fall off your chair) the user.
 * <p>
 * A user consists of a nickname
 * and a password for the nickname, among other things,
 * it carries with it a remote address in order to receive a response from the server.
 */
public class User implements Serializable {
    public User(SocketAddress userAddress) {
        this.userAddress = userAddress;
    }

    public User(SocketAddress userAddress, String nickname, String password) {
        this.userAddress = userAddress;
        this.nickname = nickname;
        this.password = password;
    }

    private SocketAddress userAddress;
    private String nickname;
    private String password;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public SocketAddress getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(SocketAddress userAddress) {
        this.userAddress = userAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userAddress, user.userAddress) && Objects.equals(nickname, user.nickname) && Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userAddress, nickname, password);
    }
}

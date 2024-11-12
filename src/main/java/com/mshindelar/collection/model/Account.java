package com.mshindelar.collection.model;

import com.mshindelar.collection.model.collection.Collection;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account implements Serializable {
    @Id
    @GeneratedValue
    private UUID id;
    private String username;
    @Column(name = "last_seen")
    private Instant lastSeen;
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Collection> collections;

//    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Friend> friends;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return id.equals(account.id) && username.equals(account.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }

    public static class Builder {
        private String name;

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Account build() {
            if (name == null) {
                throw new IllegalStateException("Missing one or more required properties");
            }

            return new Account(null, name, Instant.now(), new LinkedList<>());
        }
    }
}

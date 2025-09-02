package swypraven.complimentlabserver.domain.friend.entity;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

public enum ChatRole {
    user, system, assistant;
}
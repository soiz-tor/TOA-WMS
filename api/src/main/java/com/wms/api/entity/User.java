package com.wms.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record User(Integer id, String username, @JsonIgnore String password, Integer is_active, Integer permission) {
}

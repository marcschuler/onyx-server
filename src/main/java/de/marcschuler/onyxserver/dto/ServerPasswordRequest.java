package de.marcschuler.onyxserver.dto;

import jakarta.validation.constraints.Max;

public record ServerPasswordRequest(@Max(32) String password) {
}

package com.team42.NHPS.api.patients.utils;

import com.team42.NHPS.api.patients.data.PrescriptionEntity;
import com.team42.NHPS.api.patients.shared.PrescriptionDto;

import java.util.List;

public interface UtilService {
    <S, T> List<T> entityToDtoMapList(List<S> source, Class<T> targetClass);
}

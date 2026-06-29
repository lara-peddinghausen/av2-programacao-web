package com.github.app.model.aluno;

import org.springframework.data.jpa.repository.JpaRepository;

// ERRO 8: o segundo parâmetro de JpaRepository deve ser Integer (tipo do ID de Aluno), não String
public interface AlunoRepository extends JpaRepository<Aluno, Integer> {
}

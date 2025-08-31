package com.mar.ds.db.jpa;

import com.mar.ds.db.entity.LibHomeSequence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import javax.validation.constraints.NotNull;

@Deprecated
public interface LibHomeSeqRepository extends JpaRepository<LibHomeSequence, String> {

    Optional<LibHomeSequence> findBySeqName(@NotNull String seqName);

}

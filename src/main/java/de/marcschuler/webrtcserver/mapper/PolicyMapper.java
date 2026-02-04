package de.marcschuler.webrtcserver.mapper;

import de.marcschuler.webrtcserver.data.Server;
import de.marcschuler.webrtcserver.data.policy.Policy;
import de.marcschuler.webrtcserver.data.policy.SimplePolicy;
import de.marcschuler.webrtcserver.dto.data.policy.PolicyWriteDTO;
import de.marcschuler.webrtcserver.dto.data.policy.PolicyDTO;
import de.marcschuler.webrtcserver.dto.data.policy.SimplePolicyDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.SubclassExhaustiveStrategy;
import org.mapstruct.SubclassMapping;

@Mapper(componentModel = "spring", subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION, uses = ServerMapper.class)

public abstract class PolicyMapper {

    public abstract PolicyDTO mapToDTO(Policy policy);

    @SubclassMapping(source = SimplePolicy.class, target = SimplePolicyDTO.class)
    public abstract SimplePolicyDTO mapToDTO(SimplePolicy entity);

    @SubclassMapping(source = SimplePolicyDTO.class, target = SimplePolicy.class)
    public abstract Policy mapFromDTO(PolicyWriteDTO policyWriteDTO);

    public abstract Policy update(@MappingTarget Policy policy, PolicyWriteDTO policyWriteDTO);

}

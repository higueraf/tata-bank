package com.tata.bank;

import com.tata.bank.dto.CustomerRequestDto;
import com.tata.bank.dto.CustomerResponseDto;
import com.tata.bank.dto.DataSessionDto;
import com.tata.bank.entity.Customer;
import com.tata.bank.repository.CustomerRepository;
import com.tata.bank.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;  // Agregar Mock de PasswordEncoder

    @InjectMocks
    private CustomerService customerService;

    private CustomerRequestDto customerRequestDto;
    private Customer customer;
    private CustomerResponseDto customerResponseDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Crear un UUID válido para el usuario
        String userId = UUID.randomUUID().toString();

        // Crear un DataSessionDto simulado
        DataSessionDto dataSessionDto = new DataSessionDto();
        dataSessionDto.setUserId(userId);

        // Simular el contexto de seguridad
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(dataSessionDto);
        SecurityContextHolder.setContext(securityContext);

        // Configurar datos de prueba
        customerRequestDto = new CustomerRequestDto();
        customerRequestDto.setName("Jose Lema");
        customerRequestDto.setAddress("Otavalo sn y principal");
        customerRequestDto.setPhone("098254785");
        customerRequestDto.setPassword("1234");

        customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setName("Jose Lema");
        customer.setAddress("Otavalo sn y principal");
        customer.setPhone("098254785");

        customerResponseDto = new CustomerResponseDto();
        customerResponseDto.setId(customer.getId());
        customerResponseDto.setName("Jose Lema");
        customerResponseDto.setPhone("098254785");

        // Configurar mocks
        when(modelMapper.map(customerRequestDto, Customer.class)).thenReturn(customer);
        when(modelMapper.map(customer, CustomerResponseDto.class)).thenReturn(customerResponseDto);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("encoded-password");
    }

    @Test
    void testCreateCustomer() {
        // Ejecutar el método del servicio
        CustomerResponseDto result = customerService.createCustomer(customerRequestDto);

        // Verificar los resultados
        assertEquals(customerResponseDto.getName(), result.getName());
        assertEquals(customerResponseDto.getPhone(), result.getPhone());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }
}

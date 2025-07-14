
# Avaliação 3 – Redes de Computadores I (Parte 1/3)

**Curso:** Ciência da Computação  
**Fase:** 3ª  
**Ano/Semestre:** 2025/1  
**Disciplina:** Redes de Computadores I (RCA)  
**Data:** 07/04/2025  

## 🧩 Especificação

Desenvolva um programa de bate-papo (chat), utilizando a linguagem de programação de sua preferência, com os seguintes requisitos:

- Apenas **um único programa**, capaz de enviar e receber mensagens.
- Envio e recebimento de mensagens devem ocorrer **simultaneamente**, tratados em **threads distintas** (mesma porta de comunicação).
- A comunicação deve ser **multicast**, utilizando o protocolo **UDP**.
- O **payload** das mensagens deve ser em **formato JSON**, com o seguinte layout:

```json
{
  "date": "{{date}}",
  "time": "{{time}}",
  "username": "{{username}}",
  "message": "{{message}}"
}
```

### 🔎 Campos do JSON:
- `"date"`: data no formato **dd/mm/aaaa**, obtida no nó de origem.
- `"time"`: hora no formato **hh:mm:ss**, obtida no nó de origem.
- `"username"`: nome do usuário que enviou a mensagem.
- `"message"`: o conteúdo da mensagem.

---

# Avaliação 3 – Redes de Computadores I (Parte 2/3)

**Data:** 07/05/2025  

## 🧩 Especificação

Desenvolva um **cliente-servidor de arquivos**, similar ao FTP, utilizando o protocolo **TCP**, mas com um **protocolo próprio baseado em JSON**. 

> O servidor não precisa suportar criação ou navegação entre pastas.

### ✅ Comandos JSON

#### LIST_REQ
Solicita lista de arquivos:
```json
{ "cmd": "list_req" }
```

#### LIST_RESP
Resposta com lista de arquivos:
```json
{ "cmd": "list_resp", "files": "<file_list_vector>" }
```

#### PUT_REQ
Cliente envia arquivo:
```json
{
  "cmd": "put_req",
  "file": "<file_name>",
  "hash": "<hash_value>",
  "value": "<file_byte_base64>"
}
```

#### PUT_RESP
Confirmação de upload:
```json
{
  "cmd": "put_resp",
  "file": "<file_name>",
  "status": "<ok/fail>"
}
```

#### GET_REQ
Solicita download de arquivo:
```json
{ "cmd": "get_req", "file": "<file_name>" }
```

#### GET_RESP
Servidor responde com arquivo:
```json
{
  "cmd": "get_resp",
  "file": "<file_name>",
  "hash": "<hash_value>",
  "value": "<file_byte_base64>"
}
```

---

# Avaliação 3 – Redes de Computadores I (Parte 3/3)

**Data:** 02/05/2025  

## 📘 Contexto

Um cliente relatou o seguinte problema:

> “Temos diversas filiais com alto gasto energético devido a luzes e ar-condicionados ligados fora do expediente. Queremos monitorar e controlá-los remotamente.”

## 🎯 Solução

Criar:
- Um **servidor** (em cada filial), que controlará sensores e atuadores.
- Um **cliente** (na matriz), com interface gráfica, que monitora e envia comandos.

> O protocolo de transporte utilizado será **UDP**. A comunicação será em **formato JSON**.

---

## 🔧 Comandos JSON

### LIST – Solicita todos os ambientes:
```json
{ "cmd": "list_req" }
```
Resposta:
```json
{ "cmd": "list_resp", "place": "[sensor_actuator_vector]" }
```

### GET – Solicita estado de um dispositivo:
```json
{ "cmd": "get_req", "place": "sensor_airtemp_meetroom" }
```
Resposta:
```json
{ "cmd": "get_resp", "place": "sensor_airtemp_meetroom", "value": 22.3 }
```

### SET – Altera estado de um atuador:
```json
{ "cmd": "set_req", "locate": "actuator_air_guarita", "value": "off" }
```
Resposta:
```json
{ "cmd": "set_resp", "locate": "actuator_air_guarita", "value": "off" }
```

---

## ⚙️ Observações

- Somente **atuadores** podem ter seus valores alterados. Sensores são **somente leitura**.
- O **cliente** deve enviar requisições periódicas com intervalo configurável.
- O **cliente** deve ter **interface gráfica obrigatória**.
- O **servidor** deve carregar os dispositivos e seus valores padrão de um **arquivo JSON**:

### 📝 Exemplo de configuração inicial (JSON):
```json
{
  "actuator_light_meetroom": "off",
  "sensor_airtemp_meetroom": 0.0,
  "sensor_airhumid_meetroom": 0.0,
  "actuator_airtemp_guarita": 23.0
}
```

---

📍 **Instituto Federal de Santa Catarina – Câmpus Lages**  
Rua: Heitor Vila Lobos, 225 – São Francisco – Lages / SC – CEP: 88506-400

# Avaliação 3 – Redes de Computadores I (Parte 1/3)

**Curso:** Ciência da Computação  
**Fase:** 3ª  
**Ano/Semestre:** 2025/1  
**Disciplina:** Redes de Computadores I (RCA)  
**Data:** 07/04/2024  

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

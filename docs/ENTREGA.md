<div align="center">

<img src="icone-512.png" width="110" alt="Plexo"/>

# Plexo 1.1.0

### Dois apps. Um ecrã.

**Abra duas aplicações ao mesmo tempo, em ecrã dividido, com um único toque.**

</div>

---

## ⚠️ Leia primeiro: o passo que faz a divisão funcionar

A versão 1.0 abria **só uma app em ecrã inteiro**. Motivo: desde o **Android 12**, o sistema
ignore deliberadamente os pedidos de ecrã dividido feitos por apps normais — nenhuma app
consegue dividir o ecrã sem ajuda do sistema.

A v1.1.0 resolve isso com um **serviço de acessibilidade** (o mesmo mecanismo usado pelas
apps de referência da Play Store). É um passo único:

1. Abra o Plexo → toque em **“Ativar agora”** no cartão *“Falta 1 passo para dividir o ecrã”*.
2. Vai abrir **Definições → Acessibilidade**. Toque em **Plexo — Ecrã dividido**.
3. Ative o interruptor e confirme em **Permitir**.
4. Volte ao Plexo. Deve aparecer o selo verde **“Ecrã dividido real ativo”**.

Depois disso, o botão **“Abrir em simultâneo”** divide mesmo o ecrã.

> **Privacidade:** o serviço está configurado com `canRetrieveWindowContent=false` — o Plexo
> **não lê** o conteúdo das janelas, não vê o que escreve e não recolhe dados. Só usa duas
> coisas: a ação do sistema “dividir ecrã” e a lista de janelas (para saber se o divisor já
> apareceu). Pode desligar quando quiser nas mesmas definições.

## O que está nesta pasta

| Ficheiro | Para quê |
|---|---|
| **`Plexo-1.1.0-release.apk`** (1,19 MB) | **A app.** Otimizada (R8) e assinada. Instale esta. |
| `Plexo-1.1.0-debug.apk` (16 MB) | Versão de desenvolvimento. |
| `capturas/` | Capturas reais do app (geradas pelos testes automáticos). |
| `icone-512.png` | Ícone 512×512 para a Play Store. |
| `feature-graphic-1024x500.png` | Imagem de destaque da ficha da Play Store. |

**SHA-256 do release:**
```
eabbaf63d8f16968e1846fa19b76d757c301fd71471cb5c3af4b32e3b8d624c2
```

## Como instalar

1. Copie `Plexo-1.1.0-release.apk` para o telemóvel.
2. Abra o ficheiro → permita “instalar de origens desconhecidas” para a app de ficheiros.
3. Toque em **Instalar** → abra o **Plexo** → siga o passo de ativação acima.

> Requisitos: **Android 8.0+** (API 26). Sem internet, sem anúncios, sem contas.

## Como usar

1. **Ative o serviço** (uma única vez, ver acima).
2. Toque na **janela de cima** → escolha a app.
3. Toque na **janela de baixo** → escolha a segunda app.
4. Toque em **“Abrir em simultâneo”** — a app de cima abre, o ecrã divide-se e a de baixo
   entra na metade livre.
5. Opcional: **“Guardar par”** para repetir com um toque. Há também **widget 2×2** e
   **tile nos ajustes rápidos**.

## Se ainda não dividir

Cada fabricante muda o comportamento. Se o ecrã não dividir depois de ativar o serviço,
envie-me:

- **modelo do telemóvel** e **versão do Android** (Definições → Acerca do telefone);
- se o selo **“Ecrã dividido real ativo”** fica verde;
- o que acontece: divide e fecha? divide e mostra a app errada? não faz nada?

Com esses dados afino os tempos e a ordem das ações para o seu aparelho — o serviço já tem
margem para isso (espera pelo divisor, tempo de acomodação e nova tentativa).

## Código-fonte

**https://github.com/melven-codezx/plexo** — Kotlin + Jetpack Compose, 19 testes
automáticos, compilação contínua no GitHub Actions.

## Chave de assinatura

Mesmo keystore da v1.0 (`plexo-release.jks`); credenciais em `SIGNING.txt`.

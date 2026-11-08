<div align="center">

<img src="icone-512.png" width="110" alt="Plexo"/>

# Plexo 1.0.0

### Dois apps. Um ecrã.

**Abra duas aplicações ao mesmo tempo, em ecrã dividido, com um único toque.**

</div>

---

## O que está nesta pasta

| Ficheiro | Para quê |
|---|---|
| **`Plexo-1.0.0-release.apk`** (1,19 MB) | **A app.** Versão otimizada (R8), assinada. Instale esta. |
| `Plexo-1.0.0-debug.apk` (16 MB) | Versão de desenvolvimento, com ferramentas de diagnóstico. |
| `capturas/` | Capturas reais do app (geradas automaticamente pelos testes). |
| `icone-512.png` | Ícone 512×512 para a Play Store. |
| `feature-graphic-1024x500.png` | Imagem de destaque da ficha da Play Store. |

**SHA-256 do release:**
```
5236004611d6a27ae492f06cfb54204489d7d8bf6ec724d193c65138a0125ddf
```

## Como instalar

1. Copie `Plexo-1.0.0-release.apk` para o telemóvel (cabo USB, Bluetooth, Google Drive…).
2. Abra o ficheiro no telemóvel. O Android vai pedir para permitir instalação de
   "origens desconhecidas" — ative para a app de ficheiros que estiver a usar.
3. Toque em **Instalar**. Abra o **Plexo**.

> Requisitos: **Android 8.0 ou superior** (API 26+). Não pede nenhuma permissão
> sensível, não tem internet, não tem anúncios.

## Como usar

1. Toque na **janela de cima** → escolha a primeira app (pode pesquisar).
2. Toque na **janela de baixo** → escolha a segunda app.
3. Escolha o modo: **Ecrã dividido** ou **Flutuante**.
4. Toque em **"Abrir em simultâneo"** — as duas apps abrem lado a lado.
5. Opcional: **"Guardar par"** para abrir mais tarde com um toque.

**Extra:** mantenha o ecrã inicial premido → *Widgets* → **Plexo**, para abrir o último
par direto do ambiente de trabalho. Também há um atalho nos **ajustes rápidos**.

## Se o ecrã dividido não abrir

O Android não permite a nenhuma app forçar o ecrã dividido em todos os telemóveis.
O Plexo tenta três métodos por ordem e, se o fabricante bloquear, abre as duas apps
em sequência — depois basta arrastar uma para o topo nos recentes.

Se acontecer sempre, diga-me o **modelo do telemóvel e a versão do Android** e eu
afino o motor de lançamento para esse fabricante.

## Código-fonte

Repositório público: **https://github.com/melven-codezx/plexo**

- Kotlin + Jetpack Compose (Material 3)
- 14 testes automáticos, 100% verdes
- Compilação contínua no GitHub Actions (workflow `Android CI`)
- Ficha pronta para a Play Store em `docs/PLAY-STORE.md`

## Chave de assinatura

Para publicar atualizações, use sempre o mesmo keystore (`plexo-release.jks`).
Credenciais e instruções em `SIGNING.txt` na raiz do projeto.

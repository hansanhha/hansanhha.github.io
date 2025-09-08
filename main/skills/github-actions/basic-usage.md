---
layout: default
title:
---

[issue 생성 시 자동 라벨 부착 및 응답](#issue-생성-시-자동-라벨-부착-및-응답)


## issue 생성 시 자동 라벨 부착 및 응답

```yaml
# workflow 이름
name: issue auto response

# trigger 이벤트 지정 (issue opened)
on:
  issues:
    types: [ opened ]

# auto_response job 등록
jobs:
  auto_response:
    runs-on: ubuntu-latest # runner 지정
    permissions: # issue 수정 권한 부여
      issues: write

    # step 등록
    steps:

      # step 1. 라벨 부착
      - name: add "hello github action" label

        # github 제공 action 사용
        uses: actions/github-script@v7
        with:
          github-token: ${{ secrets.GITHUB_TOKEN }}
          script: |
            github.rest.issues.addLabels({
              owner: context.repo.owner,
              repo: context.repo.repo,
              issue_number: context.payload.issue.number,
              labels: ['hello github action']
            })

      # step 2. 댓글 생성
      - name: comment on the issue
        uses: actions/github-script@v7
        with:
          github-token: ${{ secrets.GITHUB_TOKEN }}
          script: |
            github.rest.issues.createComment({
              owner: context.repo.owner,
              repo: context.repo.repo,
              issue_number: context.payload.issue.number,
              body: 'hellllllo'
            })
```